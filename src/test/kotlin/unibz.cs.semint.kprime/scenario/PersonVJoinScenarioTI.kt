package unibz.cs.semint.kprime.scenario

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.ddl.Table
import unibz.cs.semint.kprime.usecase.VJoinUseCase
import unibz.cs.semint.kprime.usecase.XMLSerializeUseCase

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

        db.schema.key("person1", mutableSetOf(colSSN))

        db.schema.tables.add(personTable1)

        val personTable2 = Table()
        personTable2.name= "person2"

        val colT2 = Column("T", "id.T", "dbname.T")
        personTable2.columns.add(colT2)
        colT2.nullable=false

        val colS2 = Column("S", "id.S", "dbname.S")
        colS2.nullable=true
        personTable2.columns.add(colS2)

        db.schema.key("person2", mutableSetOf(colT2))

        db.schema.tables.add(personTable2)
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