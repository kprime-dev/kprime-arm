package unibz.cs.semint.kprime.scenario

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.Xrule
import unibz.cs.semint.kprime.usecase.common.XMLSerializeUseCase
import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase
import java.io.StringWriter
import java.util.*
import kotlin.test.assertEquals

class PersonTransfomerScenarioTI {

    @Test
    fun test_xpath_vertical_decomposition_on_person_db() {
        // given
        val dbFilePath = "db/person.xml"
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["originTable"]="person"
        tranformerParmeters["targetTable1"]="person1"
        tranformerParmeters["targetTable2"]="person2"

        val transfomerXml = PersonTransfomerScenarioTI::class.java.getResource("/transformer/verticalTransfomer.xml").readText()
        val vTransfomer = XMLSerializeUseCase(XMLSerializerJacksonAdapter()).deserializeTransformer(transfomerXml).ok
        val templateFilePath = vTransfomer!!.splitter.template.filename
        val xrules = toProperties(vTransfomer!!.splitter.xman.xrules)
        println(templateFilePath)
        // when
        val newDb = XPathTransformUseCase().transform(dbFilePath, templateFilePath, xrules, tranformerParmeters, StringWriter())
        // then
        val lineage = newDb.lineage("person1")
        assertEquals(lineage.size,2)
        assertEquals(lineage.last(),"person")
        assertEquals(lineage.first(),"person1")
        // print to console output
        println(newDb)
    }

    private fun toProperties(xrules: ArrayList<Xrule>): Properties {
        var pros = Properties()
        for (xrule in xrules) {
            pros[xrule.name]=xrule.rule
        }
        return pros
    }

    @Test
    // FIXME rimuovere attributi doppi dalla tabella
    // FIXME drop della primary key person1
    fun test_xpath_vertical_composition_on_person_db() {
        // given
        val dbFilePath = "db/person_splitted.xml"
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["targetTable"]="person"
        tranformerParmeters["originTable1"]="person1"
        tranformerParmeters["originTable2"]="person2"

        val transfomerXml = PersonTransfomerScenarioTI::class.java.getResource("/transformer/verticalTransfomer.xml").readText()
        val vTransfomer = XMLSerializeUseCase(XMLSerializerJacksonAdapter()).deserializeTransformer(transfomerXml).ok
        val templateFilePath = vTransfomer!!.composer.template.filename
        val xrules = toProperties(vTransfomer!!.composer.xman.xrules)
        println(templateFilePath)
        // when
        val newDb = XPathTransformUseCase().transform(dbFilePath, templateFilePath, xrules, tranformerParmeters, StringWriter())
        // then
        println(newDb)
    }


    @Test
    fun test_roundtrip_vertical_transformation() {

    }
}