package unibz.cs.semint.kprime.scenario

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import org.junit.Test
import org.w3c.dom.NodeList
import java.io.OutputStreamWriter
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class PersonXPathScenarioTI {

    @Test
    fun test_xpath_extraction_on_person_db() {
        // given
        // input person db
        val personDbXml = PersonXPathScenarioTI::class.java.getResource("/db/person.xml").readText()
        val personDbStream = PersonXPathScenarioTI::class.java.getResourceAsStream("/db/person.xml")
        val personProperties = PersonXPathScenarioTI::class.java.getResourceAsStream("/transformer/vertical/decompose/person.paths")
        //println(personDbXml)
        // input person out template
        //val personTemplate = PersonXPathScenarioTI::class.java.getResource("<database name=\"\" id=\"\">\n    <schema name=\"\" id=\"\">\n        <tables>\n            <tables name=\"person1\" id=\"\" view=\"\" condition=\"\">\n                <columns>\n                    <#list keys as key>\n                    <columns name=${key} id=\"id.${key}\" nullable=\"false\" dbtype=\"\"/>\n                    </#list>\n                    <#list lhss as lhs>\n                    <columns name=\"${lhs}\" id=\"id.${lhs}\" nullable=\"false\" dbtype=\"\"/>\n                    </#list>\n                    <#list rests as rest>\n                    <columns name=\"${rest}\" id=\"id.${rest}\" nullable=\"true\" dbtype=\"\"/>\n                    </#list>\n                </columns>\n            </tables>\n            <tables name=\"person2\" id=\"\" view=\"\" condition=\"\">\n                <columns>\n                    <#list lhss as lhs>\n                    <columns name=\"${lhs}\" id=\"id.${lhs}\" nullable=\"false\" dbtype=\"\"/>\n                    </#list>\n                    <#list rhss as rhs>\n                    <columns name=\"${rhs}\" id=\"id.${rhs}\" nullable=\"false\" dbtype=\"\"/>\n                    </#list>\n                </columns>\n            </tables>\n        </tables>\n        <constraints>\n            <constraints name=\"primaryKey.person\" id=\"\" type=\"PRIMARY_KEY\">\n                <source name=\"\" id=\"\" table=\"person\">\n                    <columns>\n                        <columns name=\"SSN\" id=\"id.SSN\" dbname=\"dbname.SSN\" nullable=\"false\" dbtype=\"\"/>\n                    </columns>\n                </source>\n                <target name=\"\" id=\"\" table=\"\">\n                    <columns/>\n                </target>\n            </constraints>\n            <constraints name=\"functional.person\" id=\"\" type=\"FUNCTIONAL\">\n                <source name=\"\" id=\"\" table=\"person\">\n                    <columns>\n                        <columns name=\"T\" id=\"id.T\" dbname=\"dbname.T\" nullable=\"false\" dbtype=\"\"/>\n                    </columns>\n                </source>\n                <target name=\"\" id=\"\" table=\"person\">\n                    <columns>\n                        <columns name=\"S\" id=\"id.S\" dbname=\"dbname.S\" nullable=\"true\" dbtype=\"\"/>\n                    </columns>\n                </target>\n            </constraints>\n        </constraints>\n    </schema>\n</database>\n").readText()
        //println(personTemplate)
        val personPaths = Properties()
        personPaths.load(personProperties)

        val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        val doc = docBuilder.parse(personDbStream)
        val xpath = XPathFactory.newInstance().newXPath()

        val xpathResult = xpath.compile("/database/schema/tables/tables/columns/columns[2]/@name").evaluate(doc)
        println(xpathResult)

        val xpathResultNodes = asValueList(xpath.compile("/database/schema/tables/tables[@name='person1']/columns/columns/@name").evaluate(doc, XPathConstants.NODESET) as NodeList)
        println(xpathResultNodes)

        // extract vars as value attributes via xpaths
        // use vars in template
        val templConfig = Configuration(Configuration.VERSION_2_3_29)
        val classTemplLoader = ClassTemplateLoader(PersonXPathScenarioTI::javaClass.javaClass,"/")
        templConfig.templateLoader= classTemplLoader
        val templModel = mutableMapOf<String,Any>()

        // compute xpath lists
        for (entryNameas in personPaths.propertyNames() )
        {
            val name = entryNameas as String
            val value = personPaths.getProperty(name)
            if (!(value.startsWith("-") || value.startsWith("+"))) {
                templModel[name] = asValueList(xpath.compile(value).evaluate(doc, XPathConstants.NODESET) as NodeList)
                println(" ${name} = ${value}")
                println(" ${name} = ${templModel[name]}")
            }
        }
        // compute derived list sum and minus
        for (entryNameas in personPaths.propertyNames() )
        {
            val name = entryNameas as String
            val value = personPaths.getProperty(name)
            if (value.startsWith("-") || value.startsWith("+")) {
                println(" ${name} = ${value}")
                templModel[name] = computeDerivedList(templModel,value)
            }
        }
        val templ = //Template.getPlainTextTemplate("templ1",personTemplate,templConfig)
                templConfig.getTemplate("transformer/vertical/decompose/person_out.template")
        val out = OutputStreamWriter(System.out)
        templ.process(templModel,out)
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
    }
}