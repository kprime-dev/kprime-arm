package unibz.cs.semint.kprime.scenario

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import freemarker.template.Template
import org.junit.Test
import java.io.OutputStreamWriter

class PersonXPathScenarioTI {

    @Test
    fun test_xpath_extraction_on_person_db() {
        // given
        // input person db
        val personDbXml = PersonXPathScenarioTI::class.java.getResource("/db/person.xml").readText()
        //println(personDbXml)
        // input person out template
        val personTemplate = PersonXPathScenarioTI::class.java.getResource("/db/person_out.template").readText()
        //println(personTemplate)
        // extract vars as value attributes via xpaths
        val keys = listOf<String>("SSN")
        val lhss = listOf<String>("T")
        val rhss = listOf<String>("S")
        val rests = listOf<String>("X")
        // use vars in template
        val templConfig = Configuration(Configuration.VERSION_2_3_29)
        val classTemplLoader = ClassTemplateLoader(PersonXPathScenarioTI::javaClass.javaClass,"/")
        templConfig.templateLoader= classTemplLoader
        val templModel = mapOf<String,Any>(
                "keys" to keys,
                "lhss" to lhss,
                "rhss" to rhss,
                "rests" to rests)
        val templ = //Template.getPlainTextTemplate("templ1",personTemplate,templConfig)
                templConfig.getTemplate("db/person_out.template")
        val out = OutputStreamWriter(System.out)
        templ.process(templModel,out)
    }
}