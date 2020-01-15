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
        val colT = Column("T", "id.T", "dbname.T")
        personTable.columns.add(colT)
        val colS = Column("S", "id.S", "dbname.S")
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
        // pure person
        val table = Table()
        table.name= "pure_person"
        personMetadata.schema.tables.add(table)
        val colSSN = Column("SSN", "id.SSN", "dbname.SSN")
        table.columns.add(colSSN)

        // person with only telephone
        val tableWithT = Table()
        tableWithT.name= "person_with_T"
        personMetadata.schema.tables.add(tableWithT)
        val colT = Column("T", "id.T", "dbname.T")
        tableWithT.columns.add(colSSN)
        tableWithT.columns.add(colT)

        // person with only telephone
        val tableWithS = Table()
        tableWithS.name= "person_with_S"
        personMetadata.schema.tables.add(tableWithS)
        val colS = Column("S", "id.S", "dbname.S")
        tableWithS.columns.add(colSSN)
        tableWithS.columns.add(colS)

        return UseCaseResult("done apply", personMetadata)
    }
}