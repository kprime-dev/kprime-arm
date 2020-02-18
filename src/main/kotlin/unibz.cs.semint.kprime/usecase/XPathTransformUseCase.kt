package unibz.cs.semint.kprime.usecase

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import org.w3c.dom.NodeList
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.ddl.Database
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStreamWriter
import java.io.Writer
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class XPathTransformUseCase {

    fun transform(dbFilePath: String, trasformerName: String, trasformerDirection: String, trasformerVersion: String, tranformerParmeters: MutableMap<String, Any>,outWriter: Writer) :Database {
        val vdecomposeFilePath = "/transformer/${trasformerName}/${trasformerDirection}/${trasformerName}_${trasformerDirection}_${trasformerVersion}.paths"
        val vdecomposeTemplatePath = "transformer/${trasformerName}/${trasformerDirection}/${trasformerName}_${trasformerDirection}_${trasformerVersion}.template"
        val personProperties = XPathTransformUseCase::class.java.getResourceAsStream(vdecomposeFilePath)
        val xPaths = Properties()
        xPaths.load(personProperties)
        return transform(dbFilePath,vdecomposeTemplatePath,xPaths, tranformerParmeters,outWriter)
    }

    fun transform(dbFilePath: String, templateFilePath: String, xPaths: Properties, tranformerParmeters: MutableMap<String, Any>,outWriter:Writer): Database {
        var dbStream : InputStream
        if (dbFilePath.startsWith("/"))
                dbStream = FileInputStream(dbFilePath)
            else
                dbStream = XPathTransformUseCase::class.java.getResourceAsStream("/${dbFilePath}")
        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.parse(dbStream)

        // extract vars as value attributes via xpaths
        // use vars in template
        val templConfig = Configuration(Configuration.VERSION_2_3_29)
        val classTemplLoader = ClassTemplateLoader(XPathTransformUseCase::javaClass.javaClass, "/")
        templConfig.templateLoader = classTemplLoader
        val templModel = mutableMapOf<String, List<String>>()

        // compute xpath lists
        val xpath = XPathFactory.newInstance().newXPath()
        var goon=true
        for (entryNameas in xPaths.propertyNames()) {
            val name = entryNameas as String
            val pathTokens = xPaths.getProperty(name).split(" ")
            val value = parametrized(pathTokens[0],tranformerParmeters)
            if (!(value.startsWith("-") || value.startsWith("+"))) {
                templModel[name] = asValueList(xpath.compile(value).evaluate(doc, XPathConstants.NODESET) as NodeList)
                println(" ${name} = ${value}")
                println(" ${name} = ${templModel[name]}")
            }
            if (pathTokens.size==3) {
                val pathCondition = pathTokens[1]
                val pathSize = pathTokens[2].toInt()
                if (pathCondition==">")
                    if ((templModel[name])!!.size <= pathSize ) goon=false
                if (pathCondition=="=")
                    if ((templModel[name])!!.size != pathSize ) goon=false
            }
        }
        if (!goon) {
            println("Condition Failure")
            return Database()
        }
        // adds all input parameters as template parameters
        for (parCouple in tranformerParmeters) {
            templModel.put(parCouple.key, listOf(parCouple.value.toString()))
        }
        // compute derived list sum and minus
        for (entryNameas in xPaths.propertyNames()) {
            val name = entryNameas as String
            val value = xPaths.getProperty(name)
            if (value.startsWith("-") || value.startsWith("+")) {
                println(" ${name} = ${value}")
                templModel[name] = computeDerivedList(templModel, value)
            }
        }
        // compute list conditions
        // = = 3 xpath
        // = > 0 xpath
        // = = 0 xpath

        val templ = //Template.getPlainTextTemplate("templ1",personTemplate,templConfig)
                templConfig.getTemplate(templateFilePath)
        templ.process(templModel, outWriter)
        val changeSetXml = outWriter.toString()
        println(changeSetXml)
        val serializer = XMLSerializerJacksonAdapter()
        val changeSet = XMLSerializeUseCase(serializer).deserializeChangeSet(changeSetXml).ok
        if (changeSet==null) { println("changeset null"); return Database()}

        val dbXml = XPathTransformUseCase::class.java.getResource("/${dbFilePath}").readText()
        val db = serializer.deserializeDatabase(dbXml)
        val newdb = ApplyChangeSetUseCase(serializer).apply(db, changeSet);

        println("-----------------------NEW-DB---------------")
        println(serializer.prettyDatabase(newdb))
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
            println(sourceLists)
            derivedList.addAll(templModel[sourceLists[0]] as List<String>)
            for (i in 1..(sourceLists.size-1)) {
                derivedList = derivedList.plus(templModel[sourceLists[i]] as MutableList<String>) as MutableList<String>
            }
            println(derivedList)
        }
        if (splittedRule[0]=="-") {
            //println(splittedRule)
            val sourceLists = splittedRule.drop(1)
            //println(sourceLists)
            derivedList.addAll(templModel[sourceLists[0]] as List<String>)
            for (i in 1..(sourceLists.size-1)) {
                derivedList = derivedList.minus(templModel[sourceLists[i]] as MutableList<String>) as MutableList<String>
            }
            println(" $derivedList")
        }
        // if derivationRule starts with - then compute intersection
        return derivedList
    }

    private fun asValueList(xpathResultNodes: NodeList): MutableList<String> {
        val listNodeValues = mutableListOf<String>()
        for (nodeId in 0..xpathResultNodes.length) {
            val item = xpathResultNodes.item(nodeId)
            if (item==null) continue
            listNodeValues.add(item.nodeValue)
        }
        return listNodeValues
    }}