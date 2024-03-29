package unibz.cs.semint.kprime.scenario.person

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Table
import unibz.cs.semint.kprime.usecase.current.VJoinUseCase
import unibz.cs.semint.kprime.usecase.common.XMLSerializeUseCase

class PersonVJoinScenarioTI {

    private fun buildMetadata(): Database {
        val db = Database()
        val personTable1 = Table()
        personTable1.name= "person1"
        val colSSN = Column("SSN", "id.SSN", "dbname.SSN")
        personTable1.columns.add(colSSN)
        colSSN.nullable=false

        val colT = Column("T", "id.T", "dbname.T")
        personTable1.columns.add(colT)
        colT.nullable=false

        val colX = Column("X", "id.X", "dbname.X")
        colX.nullable=true
        personTable1.columns.add(colX)

        db.schema.addKey("person1", mutableSetOf(colSSN))

        db.schema.tables().add(personTable1)

        val personTable2 = Table()
        personTable2.name= "person2"

        val colT2 = Column("T", "id.T", "dbname.T")
        personTable2.columns.add(colT2)
        colT2.nullable=false

        val colS2 = Column("S", "id.S", "dbname.S")
        colS2.nullable=true
        personTable2.columns.add(colS2)

        db.schema.addKey("person2", mutableSetOf(colT2))

        db.schema.tables().add(personTable2)
        return db
    }

    @Test
    fun test_person_vsplit_scenario() {
        // given
        val personVJoinUseCase = VJoinUseCase()
        // when
        val changeSet = personVJoinUseCase.compute(buildMetadata())
        // then
        // prints changeset
        println(XMLSerializeUseCase(XMLSerializerJacksonAdapter()).prettyChangeSet(changeSet))
    }
}