package unibz.cs.semint.kprime.scenario.person

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.dtl.Xrule
import unibz.cs.semint.kprime.usecase.common.XMLSerializeUseCase
import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase
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
        val xrules = Xrule.toListOfString(vTransfomer.splitter.xman.xrules)
        println(templateFilePath)
        // when
        val newDb = XPathTransformUseCase().transform(dbFilePath, templateFilePath, xrules, tranformerParmeters)
        // then
        val lineage = newDb.lineage("person1")
        assertEquals(lineage.size,2)
        assertEquals(lineage.last(),"person")
        assertEquals(lineage.first(),"person1")
        // print to console output
        println(newDb)
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
        val xrules = Xrule.toListOfString(vTransfomer.composer.xman.xrules)
        println(templateFilePath)
        // when
        val newDb = XPathTransformUseCase().transform(dbFilePath, templateFilePath, xrules, tranformerParmeters)
        // then
        println(newDb)
    }


    @Test
    fun test_roundtrip_vertical_transformation() {

    }
}