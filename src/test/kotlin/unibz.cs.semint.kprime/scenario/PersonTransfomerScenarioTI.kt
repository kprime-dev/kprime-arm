package unibz.cs.semint.kprime.scenario

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.Xrule
import unibz.cs.semint.kprime.usecase.XMLSerializeUseCase
import unibz.cs.semint.kprime.usecase.XPathTransformUseCase
import java.io.OutputStreamWriter
import java.io.StringWriter
import java.util.*

class PersonTransfomerScenarioTI {

    @Test
    fun test_xpath_vertical_decomposition_on_person_db() {
        // given
        val dbFilePath = "db/person.xml"
        val transfomerXml = PersonTransfomerScenarioTI::class.java.getResource("/transformer/verticalTransfomer.xml").readText()
        val vTransfomer = XMLSerializeUseCase(XMLSerializerJacksonAdapter()).deserializeTransformer(transfomerXml).ok
        val templateFilePath = vTransfomer!!.splitter.template.filename
        val xrules = toProperties(vTransfomer!!.splitter.xman.xrules)
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["table"]="person"
        println(templateFilePath)
        // when
        XPathTransformUseCase().transform(dbFilePath, templateFilePath, xrules,tranformerParmeters, StringWriter())
        // then
        // print to console output
    }

    private fun toProperties(xrules: ArrayList<Xrule>): Properties {
        var pros = Properties()
        for (xrule in xrules) {
            pros[xrule.name]=xrule.rule
        }
        return pros
    }

    @Test
    fun test_xpath_horizontal_decomposition_on_person_db() {
        /*
        // given
        val dbFilePath = "db/person.xml"
        val trasformerName = "horizontal"
        val trasformerDirection = "decompose"
        val trasformerVersion = "1"
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["table"]="person"
        // when
        XPathTransformUseCase().transform(dbFilePath, trasformerName, trasformerDirection, trasformerVersion,tranformerParmeters,OutputStreamWriter(System.out))
        // then
        // print to console output
         */
    }


}