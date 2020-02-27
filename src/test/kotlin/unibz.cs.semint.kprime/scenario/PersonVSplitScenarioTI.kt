package unibz.cs.semint.kprime.scenario

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.ddl.Table
import unibz.cs.semint.kprime.usecase.current.VSplitUseCase
import unibz.cs.semint.kprime.usecase.common.XMLSerializeUseCase

class PersonVSplitScenarioTI {

    private fun buildPersonMetadata(): Database {
        val db = Database()
        val personTable = Table()
        personTable.name= "person"
        val colSSN = Column("SSN", "id.SSN", "dbname.SSN")
        personTable.columns.add(colSSN)
        colSSN.nullable=false

        val colT = Column("T", "id.T", "dbname.T")
        personTable.columns.add(colT)
        colT.nullable=false

        val colS = Column("S", "id.S", "dbname.S")
        colS.nullable=true
        personTable.columns.add(colS)

        val colX = Column("X", "id.X", "dbname.X")
        colX.nullable=true
        personTable.columns.add(colX)

        db.schema.key("person", mutableSetOf(colSSN))
        db.schema.functional("person", mutableSetOf(colT), mutableSetOf(colS))

        db.schema.tables.add(personTable)

        return db
    }



    @Test
    fun test_person_vsplit_scenario() {
        // given
        val database = buildPersonMetadata()
        val personVSplitUseCase = VSplitUseCase()
        // when
        val changeSet = personVSplitUseCase.compute(database)
        // then
        println(XMLSerializeUseCase(XMLSerializerJacksonAdapter()).prettyChangeSet(changeSet))
    }

    @Test
    fun test_print_input_db() {
        val db = buildPersonMetadata()
        println(XMLSerializeUseCase(XMLSerializerJacksonAdapter()).prettyDatabase(db).ok)
    }
}