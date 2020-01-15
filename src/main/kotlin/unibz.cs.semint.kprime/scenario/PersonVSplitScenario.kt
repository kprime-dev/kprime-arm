package unibz.cs.semint.kprime.scenario

import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.*
import unibz.cs.semint.kprime.usecase.XMLSerializeUseCase

class PersonVSplitScenario {

    fun run() {
        val personMetadata = buildPersonMetadata()
        vsplitPersonMetadata(personMetadata)

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
        primaryConstraint.type=Constraint.TYPE.PRIMARY_KEY.name
        db.schema.constraints.add(primaryConstraint)
        return db
    }

    private fun vsplitPersonMetadata(personMetadata: Database) {
        printDb(personMetadata)
        val  detected = detect(personMetadata)
        if (detected.ok!=null) {
            val applied = apply(personMetadata, detected)
            if (applied.ok!=null) printDb(applied.ok)
        }
    }

    private fun printDb(db:Database) {
        println()
        println("--------------------------------------------------------------------------")
        println(XMLSerializeUseCase(XMLSerializerJacksonAdapter()).prettyDatabase(db))
    }

    private fun detect(personMetadata: Database): UseCaseResult<Database> {
        return UseCaseResult("done detect",personMetadata)
    }

    private fun apply(personMetadata: Database, detected: UseCaseResult<Database>): UseCaseResult<Database>{
        return UseCaseResult("done apply", personMetadata)
    }
}