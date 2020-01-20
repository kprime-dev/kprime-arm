package unibz.cs.semint.kprime.scenario

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.usecase.XMLSerializeUseCase

class PersonVSplitScenarioTI {

    @Test
    fun test_person_vsplit_scenario() {
        // given
        val personVSplitScenario = PersonVSplitScenario()
        // when
        val changeSet = personVSplitScenario.run()
        // then
        // prints changeset
        println(XMLSerializeUseCase(XMLSerializerJacksonAdapter()).prettyChangeSet(changeSet))
    }
}