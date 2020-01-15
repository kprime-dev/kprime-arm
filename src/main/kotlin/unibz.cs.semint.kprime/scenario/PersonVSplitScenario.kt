package unibz.cs.semint.kprime.scenario

import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.Column
import unibz.cs.semint.kprime.domain.Constraint
import unibz.cs.semint.kprime.domain.Database
import unibz.cs.semint.kprime.domain.Table
import unibz.cs.semint.kprime.usecase.XMLSerializeUseCase

class PersonVSplitScenario {

    fun run() {
        val personMetadata = buildPersonMetadata()
        print(XMLSerializeUseCase(XMLSerializerJacksonAdapter()).prettyDatabase(personMetadata).ok)
        //vsplitSakila(personMetadata)

    }

    private fun vsplitSakila(personMetadata: Database) {
        TODO("not implemented")
        // if detect(db):result
        //  apply(db,result):db
    }

    private fun buildPersonMetadata(): Database {
        val db = Database()
        val personTable = Table()
        personTable.name= "person"
        val colSSN = Column("SSN", "id.SSN", "dbname.SSN")
        personTable.columns.add(colSSN)
        val colT = Column("T", "id.SSN", "dbname.SSN")
        personTable.columns.add(colT)
        val colS = Column("S", "id.SSN", "dbname.SSN")
        personTable.columns.add(colS)
        db.schema.tables.add(personTable)
        val primaryConstraint = Constraint()
        primaryConstraint.name="primaryKey.person"
        primaryConstraint.source.columns.add(colSSN)
        primaryConstraint.source.columns.add(colT)
        db.schema.constraints.add(primaryConstraint)
        return db
    }

}