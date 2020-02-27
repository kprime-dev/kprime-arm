package unibz.cs.semint.kprime.scenario

import org.junit.Test
import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase
import java.io.OutputStreamWriter

class PersonXPathScenarioTI {

    @Test
    fun test_xpath_vertical_decomposition_on_person_db() {
        // given
        val dbFilePath = "db/person.xml"
        val trasformerName = "vertical"
        val trasformerDirection = "decompose"
        val trasformerVersion = "1"
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["originTable"]="person"
        tranformerParmeters["targetTable1"]="person1"
        tranformerParmeters["targetTable2"]="person2"
        // when
        XPathTransformUseCase().transform(dbFilePath, trasformerName, trasformerDirection, trasformerVersion,tranformerParmeters, OutputStreamWriter(System.out))
        // then
        // print to console output
    }

    @Test
    fun test_xpath_horizontal_decomposition_on_person_db() {
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
    }


}