package unibz.cs.semint.kprime.scenario

import org.junit.Test

class SakilaScenarioTI {

    @Test
    fun test_sakila_scenario() {
        // given
        val sakilaScenario = SakilaScenario()
        // when
        sakilaScenario.run()
        // then
        // prints manipulated sakila database
    }
}