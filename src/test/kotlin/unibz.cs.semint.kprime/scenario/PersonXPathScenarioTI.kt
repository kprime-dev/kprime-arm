package unibz.cs.semint.kprime.scenario

import org.junit.Test
import unibz.cs.semint.kprime.usecase.XPathTransformUseCase

class PersonXPathScenarioTI {

    @Test
    fun test_xpath_vertical_decomposition_on_person_db() {
        // given
        val dbFilePath = "db/person.xml"
        val trasformerName = "vertical"
        val trasformerDirection = "decompose"
        val trasformerVersion = "1"
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["table"]="person"
        // when
        XPathTransformUseCase().transform(dbFilePath, trasformerName, trasformerDirection, trasformerVersion,tranformerParmeters)
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
        XPathTransformUseCase().transform(dbFilePath, trasformerName, trasformerDirection, trasformerVersion,tranformerParmeters)
        // then
        // print to console output
    }


}