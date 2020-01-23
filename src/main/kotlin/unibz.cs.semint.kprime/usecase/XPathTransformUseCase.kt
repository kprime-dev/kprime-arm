package unibz.cs.semint.kprime.usecase

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import org.w3c.dom.NodeList
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStreamWriter
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class XPathTransformUseCase {

    fun transform(dbFilePath: String, trasformerName: String, trasformerDirection: String, trasformerVersion: String) {

        val vdecomposeFilePath = "/transformer/${trasformerName}/${trasformerDirection}/${trasformerName}_${trasformerDirection}_${trasformerVersion}.paths"
        val vdecomposeTemplatePath = "transformer/${trasformerName}/${trasformerDirection}/${trasformerName}_${trasformerDirection}_${trasformerVersion}.template"

        val personProperties = XPathTransformUseCase::class.java.getResourceAsStream(vdecomposeFilePath)
        val personPaths = Properties()
        personPaths.load(personProperties)

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
        val templModel = mutableMapOf<String, Any>()

        // compute xpath lists
        val xpath = XPathFactory.newInstance().newXPath()
        for (entryNameas in personPaths.propertyNames()) {
            val name = entryNameas as String
            val value = personPaths.getProperty(name)
            if (!(value.startsWith("-") || value.startsWith("+"))) {
                templModel[name] = asValueList(xpath.compile(value).evaluate(doc, XPathConstants.NODESET) as NodeList)
                println(" ${name} = ${value}")
                println(" ${name} = ${templModel[name]}")
            }
        }
        // compute derived list sum and minus
        for (entryNameas in personPaths.propertyNames()) {
            val name = entryNameas as String
            val value = personPaths.getProperty(name)
            if (value.startsWith("-") || value.startsWith("+")) {
                println(" ${name} = ${value}")
                templModel[name] = computeDerivedList(templModel, value)
            }
        }
        val templ = //Template.getPlainTextTemplate("templ1",personTemplate,templConfig)
                templConfig.getTemplate(vdecomposeTemplatePath)
        templ.process(templModel, OutputStreamWriter(System.out))
    }


    private fun computeDerivedList(templModel: MutableMap<String, Any>, derivationRule: String): Any {
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