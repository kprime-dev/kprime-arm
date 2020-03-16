package unibz.cs.semint.kprime.support

import freemarker.template.Configuration
import freemarker.template.Template
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.io.StringWriter

class FreemarkerTI {

    @Test
    @Ignore
    fun test_freemarker_templating_file() {
        // given
        val templateFilePath = "/home/nipe/Temp/kprime/transformers/vertical/decompose/vertical_decompose_1_changeset.xml"
        val templConfig = Configuration(Configuration.VERSION_2_3_29)
        lateinit var templ : Template
        if (templateFilePath.startsWith("/")||
                templateFilePath.startsWith("./")) {
            val readText = File(templateFilePath).readText(Charsets.UTF_8)
//            val readText = """
//                <#list keys as key>
//                <columns name="//$//{key}" id="id.//$//{key}" dbname="" nullable="false" dbtype=""/>
//                </#list>
//            """.trimIndent()
            templ = Template.getPlainTextTemplate("template1",
                    readText, templConfig)
        } else {
            templ = templConfig.getTemplate(templateFilePath)
        }
        val templModel = mutableMapOf<String, List<String>>(
                "keys" to listOf("Uno","Due")
        )
        val outWriter = StringWriter()
        // when
        templ.process(templModel, outWriter)
        // then
        val changeSetXml = outWriter.buffer.toString()
        println(changeSetXml)

    }
}