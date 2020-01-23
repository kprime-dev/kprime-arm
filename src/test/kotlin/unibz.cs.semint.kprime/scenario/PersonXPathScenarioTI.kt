package unibz.cs.semint.kprime.scenario

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import org.junit.Test
import org.w3c.dom.NodeList
import unibz.cs.semint.kprime.usecase.XPathTransformUseCase
import java.io.OutputStreamWriter
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class PersonXPathScenarioTI {

    @Test
    fun test_xpath_extraction_on_person_db() {
        // given
        val dbFilePath = "db/person.xml"
        val trasformerName = "vertical"
        val trasformerDirection = "decompose"
        val trasformerVersion = "1"
        // when
        XPathTransformUseCase().transform(dbFilePath, trasformerName, trasformerDirection, trasformerVersion)
        // then
        // print to console output
    }


}