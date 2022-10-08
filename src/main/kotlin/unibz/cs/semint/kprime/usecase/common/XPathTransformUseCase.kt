package unibz.cs.semint.kprime.usecase.common

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import org.w3c.dom.NodeList
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.usecase.service.FileIOServiceI
import java.io.File
import java.io.StringWriter
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class XPathTransformUseCase  {

    fun transform(
            dbFilePath: String,
            trasformerName: String,
            trasformerDirection: String,
            trasformerVersion: String,
            tranformerParmeters: MutableMap<String, Any>)
            :Database {
        val vdecomposeFilePath = "/transformer/${trasformerName}/${trasformerDirection}/${trasformerName}_${trasformerDirection}_${trasformerVersion}.paths"
        val vdecomposeTemplatePath = "transformer/${trasformerName}/${trasformerDirection}/${trasformerName}_${trasformerDirection}_${trasformerVersion}.template"
        val xPaths = File(vdecomposeFilePath).readLines()
        return transform(dbFilePath,vdecomposeTemplatePath,xPaths, tranformerParmeters)
    }

    /*
    It uses 'changeset' template
     */
    fun compute(
            dbFilePath: String,
            trasformerName: String,
            trasformerDirection: String,
            trasformerVersion: String,
            tranformerParmeters: MutableMap<String, Any>)
            :ChangeSet {
        val vdecomposeFilePath = "/transformer/${trasformerName}/${trasformerDirection}/${trasformerName}_${trasformerDirection}_${trasformerVersion}.paths"
        val vdecomposeTemplatePath = "transformer/${trasformerName}/${trasformerDirection}/${trasformerName}_changeset_${trasformerVersion}.template"
        val xPaths = File(vdecomposeFilePath).readLines()
        return compute(dbFilePath, vdecomposeTemplatePath, xPaths, tranformerParmeters)
    }

    fun transform(
            dbFilePath: String,
            templateFilePath: String,
            xPaths: List<String>,
            tranformerParmeters:
            MutableMap<String, Any>)
            : Database {
        val changeSet = compute(dbFilePath, templateFilePath, xPaths, tranformerParmeters)

        //val dbXml = XPathTransformUseCase::class.java.getResource("/${dbFilePath}").readText()
        val dbXml = FileIOServiceI.readString(FileIOServiceI.inputStreamFromPath(dbFilePath))
        val serializer = XMLSerializerJacksonAdapter()
        val db = serializer.deserializeDatabase(dbXml)
        val newdb = ApplyChangeSetUseCase(serializer).apply(db, changeSet)

//        println("-----------------------NEW-DB---------------")
        //println(serializer.prettyDatabase(newdb))
        return newdb
    }

    private fun parametrized(line: String, tranformerParmeters: MutableMap<String, Any>): String {
        var newline = line
        for (key in tranformerParmeters.keys) {
            val newValue = (tranformerParmeters[key] as List<String>).get(0)
            //println(" *************************** XPathTransformUseCase.parametrized(): ${key} == ${newValue}")
            newline = newline.replace("${key}", newValue)
            //println(newline)
        }
        return newline
    }


    private fun computeDerivedList(templModel: MutableMap<String, List<String>>, derivationRule: String): List<String> {
        var derivedList = mutableListOf<String>()
        // if derivationRule starts with + then compute union
        var splittedRule = derivationRule.split(" ")
        splittedRule = removeEventualConditionToken(splittedRule)
        //println("splittedRule ( ${splittedRule } )")
        if (splittedRule[0]=="+") {
            val sourceLists = splittedRule.drop(1)
            //println("sourceLists:"+sourceLists)
            derivedList.addAll(templModel[sourceLists[0]] as List<String>)
            for (i in 1..(sourceLists.size-1)) {
                if (templModel!=null
                        && sourceLists!=null
                        && sourceLists[i]!=null
                        && templModel[sourceLists[i]]!= null
                        && !templModel[sourceLists[i]]!!.isEmpty())
                    derivedList = derivedList.plus(templModel[sourceLists[i]] as MutableList<String>) as MutableList<String>
            }
            //println(derivedList)
        }
        if (splittedRule[0]=="-") {
            //println(splittedRule)
            val sourceLists = splittedRule.drop(1)
            //println("sourceLists:"+sourceLists)
            derivedList.addAll(templModel[sourceLists[0]] as List<String>)
            for (i in 1..(sourceLists.size-1)) {
                if (!templModel[sourceLists[i]]!!.isEmpty())
                    derivedList = derivedList.minus(templModel[sourceLists[i]] as MutableList<String>) as MutableList<String>
            }
            //println(" $derivedList")
        }
        // if derivationRule starts with - then compute intersection
        return derivedList.toSet().toList()
    }

    private fun removeEventualConditionToken(splittedRule: List<String>): List<String> {
        val conditionOperators = listOf("=",">")
        if (conditionOperators.contains(splittedRule[splittedRule.size-2]))
            return splittedRule.subList(0,splittedRule.size-2)
        return splittedRule
    }

    private fun asValueList(xpathResultNodes: NodeList): MutableList<String> {
        val listNodeValues = mutableListOf<String>()
        for (nodeId in 0..xpathResultNodes.length) {
            val item = xpathResultNodes.item(nodeId)
            if (item==null) continue
            listNodeValues.add(item.nodeValue)
        }
        //println("listNodeValues:"+listNodeValues.toString())
        return listNodeValues
    }

    fun compute(
            dbFilePath: String,
            templateFilePath: String,
            xPaths: List<String>,
            tranformerParmeters: MutableMap<String, Any>)
            : ChangeSet {

        val pair = getTemplateModel(dbFilePath, xPaths, tranformerParmeters)
        val templModel = pair.first
        var violation = pair.second

        if (!violation.isEmpty()) {
            println("Condition Failure")
            println(violation)
            return ChangeSet()
        }

//        println("22++++++++++++++++++++++++++++++++++++++++++-------------------------------")

        val templConfig = Configuration(Configuration.VERSION_2_3_29)
        val classTemplLoader = ClassTemplateLoader(XPathTransformUseCase::javaClass.javaClass, "/")
        templConfig.templateLoader = classTemplLoader

        lateinit var templ : Template
        if (templateFilePath.startsWith("/")||
                templateFilePath.startsWith("./")) {
            templ = Template.getPlainTextTemplate("template1",
                    File(templateFilePath).readText(Charsets.UTF_8), templConfig)
            val lastSlash = templateFilePath.lastIndexOf("/")
            val templateDir = templateFilePath.substring(0,lastSlash)
            val templateFileName = templateFilePath.substring(lastSlash)
            //templConfig.setDirectoryForTemplateLoading(File("/home/nipe/Temp/kprime/transformers/vertical/decompose/"))
            //templ = templConfig.getTemplate("vertical_decompose_1_changeset.xml")
            //println("${templateDir}:${templateFileName}")
            templConfig.setDirectoryForTemplateLoading(File(templateDir))
            templ = templConfig.getTemplate(templateFileName)
        } else {
            templ = templConfig.getTemplate(templateFilePath)
        }
        val outWriter = StringWriter()
//        for(ent in templModel) {
//            println("${ent.key} === ${ent.value}")
//        }
        templ.process(templModel, outWriter)

//        println("33++++++++++++++++++++++++++++++++++++++++++-------------------------------")
        val changeSetXml = outWriter.buffer.toString()
//        println(changeSetXml)
//        println("44++++++++++++++++++++++++++++++++++++++++++-------------------------------")
        val serializer = XMLSerializerJacksonAdapter()
        val changeSet = XMLSerializeUseCase(serializer).deserializeChangeSet(changeSetXml).ok ?: ChangeSet()
//        println("55++++++++++++++++++++++++++++++++++++++++++-------------------------------")

        return changeSet
    }

    fun getTemplateModel(dbFilePath: String, xPaths: List<String>, tranformerParameters: MutableMap<String, Any>): Pair<MutableMap<String, List<String>>, String> {
        val templModel = mutableMapOf<String, List<String>>()

        // compute xpath lists
        //val dbInputStream: InputStream = FileIOService.inputStreamFromPath(dbFilePath)
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        //println("dbFilePath: ${dbFilePath}")
        val doc = docBuilder.parse(File(dbFilePath))
        val xpath = XPathFactory.newInstance().newXPath()
        var violation = ""
        for (xPathLine in xPaths) {
            //println("------------------------------------------")
            if (xPathLine.startsWith("((")) continue
            val xPathTokens = xPathLine.split("==")
            val name = xPathTokens[0]
            val rule = xPathTokens[1]
            val pathTokens = rule.split(" ")
            val value = parametrized(pathTokens[0], tranformerParameters)
            if (value.startsWith("-") || value.startsWith("+")) {
                templModel[name] = computeDerivedList(templModel, rule)
//                violation = checkCondition(pathTokens, templModel, name, violation, rule)
            }
            else {
//                println("getTemplateModel().xpath.compile(value)="+value)
                templModel[name] = asValueList(xpath.compile(value).evaluate(doc, XPathConstants.NODESET) as NodeList)
                //println(" ${name} = ${value}")
                //println(" ${name} = ${templModel[name]}")
                if (!templModel[name]!!.isEmpty()) tranformerParameters[name] = templModel[name]!!
                violation = checkCondition(pathTokens, templModel, name, violation, rule)
            }
        }
        // adds all input parameters as template parameters
        for (parCouple in tranformerParameters) {
            //println("parCouple.key:::::::::::"+parCouple.key)
            if (parCouple.value is List<*>)
                templModel.put(parCouple.key, parCouple.value as List<String>)
            else
                templModel.put(parCouple.key, listOf(parCouple.value.toString()))
        }
        return Pair(templModel, violation)
    }

    private fun checkCondition(pathTokens: List<String>, templModel: MutableMap<String, List<String>>, name: String, violation: String, rule: String): String {
        var violation1 = violation
        if (pathTokens.size > 2) {
            val pathCondition = pathTokens[pathTokens.size - 2]
            if (pathCondition == ">") {
                val pathSize = pathTokens[pathTokens.size - 1].toInt()
                if ((templModel[name])!!.size <= pathSize) violation1 = "violation: ${name}:${rule} ${templModel[name]} size <= ${pathSize}"
            }
            if (pathCondition == "=") {
                val pathSize = pathTokens[pathTokens.size - 1].toInt()
                if ((templModel[name])!!.size != pathSize) violation1 = "violation: ${name}:${rule} ${templModel[name]} size != ${pathSize}"
            }
        }
        return violation1
    }
}