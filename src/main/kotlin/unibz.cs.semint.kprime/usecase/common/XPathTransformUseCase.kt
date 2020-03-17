package unibz.cs.semint.kprime.usecase.common

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import org.w3c.dom.NodeList
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.usecase.service.FileIOService
import java.io.File
import java.io.InputStream
import java.io.StringWriter
import java.util.*
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
        val personProperties = XPathTransformUseCase::class.java.getResourceAsStream(vdecomposeFilePath)
        val xPaths = Properties()
        xPaths.load(personProperties)
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
        val personProperties = XPathTransformUseCase::class.java.getResourceAsStream(vdecomposeFilePath)
        val xPaths = Properties()
        xPaths.load(personProperties)
        return compute(dbFilePath, vdecomposeTemplatePath, xPaths, tranformerParmeters)
    }

    fun transform(
            dbFilePath: String,
            templateFilePath: String,
            xPaths: Properties,
            tranformerParmeters:
            MutableMap<String, Any>)
            : Database {
        val changeSet = compute(dbFilePath, templateFilePath, xPaths, tranformerParmeters)

        //val dbXml = XPathTransformUseCase::class.java.getResource("/${dbFilePath}").readText()
        val dbXml = FileIOService.readString(FileIOService.inputStreamFromPath(dbFilePath))
        val serializer = XMLSerializerJacksonAdapter()
        val db = serializer.deserializeDatabase(dbXml)
        val newdb = ApplyChangeSetUseCase(serializer).apply(db, changeSet)

        println("-----------------------NEW-DB---------------")
        //println(serializer.prettyDatabase(newdb))
        return newdb
    }

    private fun parametrized(line: String, tranformerParmeters: MutableMap<String, Any>): String {
        var newline = line
        for (key in tranformerParmeters.keys) {
            newline = newline.replace("%%${key}%%", tranformerParmeters[key] as String)
        }
        return newline
    }


    private fun computeDerivedList(templModel: MutableMap<String, List<String>>, derivationRule: String): List<String> {
        var derivedList = mutableListOf<String>()
        // if derivationRule starts with + then compute union
        val splittedRule = derivationRule.split(" ")
        if (splittedRule[0]=="+") {
            val sourceLists = splittedRule.drop(1)
            println("sourceLists:"+sourceLists)
            derivedList.addAll(templModel[sourceLists[0]] as List<String>)
            for (i in 1..(sourceLists.size-1)) {
                if (!templModel[sourceLists[i]]!!.isEmpty())
                    derivedList = derivedList.plus(templModel[sourceLists[i]] as MutableList<String>) as MutableList<String>
            }
            println(derivedList)
        }
        if (splittedRule[0]=="-") {
            //println(splittedRule)
            val sourceLists = splittedRule.drop(1)
            println("sourceLists:"+sourceLists)
            derivedList.addAll(templModel[sourceLists[0]] as List<String>)
            for (i in 1..(sourceLists.size-1)) {
                if (!templModel[sourceLists[i]]!!.isEmpty())
                    derivedList = derivedList.minus(templModel[sourceLists[i]] as MutableList<String>) as MutableList<String>
            }
            println(" $derivedList")
        }
        // if derivationRule starts with - then compute intersection
        return derivedList.toSet().toList()
    }

    private fun asValueList(xpathResultNodes: NodeList): MutableList<String> {
        val listNodeValues = mutableListOf<String>()
        for (nodeId in 0..xpathResultNodes.length) {
            val item = xpathResultNodes.item(nodeId)
            if (item==null) continue
            listNodeValues.add(item.nodeValue)
        }
        return listNodeValues
    }

    fun compute(
            dbFilePath: String,
            templateFilePath: String,
            xPaths: Properties,
            tranformerParmeters: MutableMap<String, Any>)
            : ChangeSet {

        val pair = getTemplateModel(dbFilePath, xPaths, tranformerParmeters)
        val templModel = pair.first
        var violation = pair.second

        if (!violation.isEmpty()) {
            println("Condition Failure")
            return ChangeSet()
        }

        println("22++++++++++++++++++++++++++++++++++++++++++-------------------------------")

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
            println("${templateDir}:${templateFileName}")
            templConfig.setDirectoryForTemplateLoading(File(templateDir))
            templ = templConfig.getTemplate(templateFileName)
        } else {
            templ = templConfig.getTemplate(templateFilePath)
        }
        val outWriter = StringWriter()
        templ.process(templModel, outWriter)

        println("33++++++++++++++++++++++++++++++++++++++++++-------------------------------")
        val changeSetXml = outWriter.buffer.toString()
        println(changeSetXml)
        println("44++++++++++++++++++++++++++++++++++++++++++-------------------------------")
        val serializer = XMLSerializerJacksonAdapter()
        val changeSet = XMLSerializeUseCase(serializer).deserializeChangeSet(changeSetXml).ok ?: ChangeSet()
        println("55++++++++++++++++++++++++++++++++++++++++++-------------------------------")

        return changeSet
    }

    fun getTemplateModel(dbFilePath: String, xPaths: Properties, tranformerParmeters: MutableMap<String, Any>): Pair<MutableMap<String, List<String>>, String> {
        val templModel = mutableMapOf<String, List<String>>()

        // compute xpath lists
        val dbInputStream: InputStream = FileIOService.inputStreamFromPath(dbFilePath)
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.parse(dbInputStream)
        val xpath = XPathFactory.newInstance().newXPath()
        var violation = ""
        for (entryNameas in xPaths.propertyNames()) {
            val name = entryNameas as String
            val pathTokens = xPaths.getProperty(name).split(" ")
            val value = parametrized(pathTokens[0], tranformerParmeters)
            if (!(value.startsWith("-") || value.startsWith("+"))) {
                templModel[name] = asValueList(xpath.compile(value).evaluate(doc, XPathConstants.NODESET) as NodeList)
                println(" ${name} = ${value}")
                println(" ${name} = ${templModel[name]}")
                if (!templModel[name]!!.isEmpty())
                tranformerParmeters[name] = templModel[name]!![0]
                if (pathTokens.size == 3) {
                    println(pathTokens)
                    val pathCondition = pathTokens[1]
                    val pathSize = pathTokens[2].toInt()
                    if (pathCondition == ">")
                        if ((templModel[name])!!.size <= pathSize) violation = "violation: ${templModel[name]} size <= ${pathSize}"
                    if (pathCondition == "=")
                        if ((templModel[name])!!.size != pathSize) violation = "violation: ${templModel[name]} size != ${pathSize}"
                }
            }
        }
        // adds all input parameters as template parameters
        for (parCouple in tranformerParmeters) {
            templModel.put(parCouple.key, listOf(parCouple.value.toString()))
        }
        // compute derived list sum and minus
        // if there are values in template model
        if (!templModel.isEmpty()) {
            for (entryNameas in xPaths.propertyNames()) {
                val name = entryNameas as String
                val value = xPaths.getProperty(name)
                if (value.startsWith("-") || value.startsWith("+")) {
                    println(" ${name} = ${value}")
                    templModel[name] = computeDerivedList(templModel, value)
                }
            }
        }
        return Pair(templModel, violation)
    }
}