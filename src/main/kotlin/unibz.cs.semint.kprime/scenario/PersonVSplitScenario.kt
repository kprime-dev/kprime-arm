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
        colSSN.nullable=false
        val colT = Column("T", "id.T", "dbname.T")
        personTable.columns.add(colT)
        colT.nullable=false
        val colS = Column("S", "id.S", "dbname.S")
        colS.nullable=true
        personTable.columns.add(colS)
        db.schema.tables.add(personTable)
        val primaryConstraint = Constraint()
        primaryConstraint.name="primaryKey.person"
        primaryConstraint.source.table="person"
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
        val dbDetected = Database()
        // Check if it has nullable column.
        for (table in personMetadata.schema.tables) {
            if (table.hasNullable()) {
                // then this table has to be partitioned to remove nullable.
                // adds this table to db to apply vertical partitioning.
                dbDetected.schema.tables.add(table)
            }
        }
        return UseCaseResult("done detect",dbDetected)
    }

    private fun apply(personMetadata: Database, detected: UseCaseResult<Database>): UseCaseResult<Database>{
        // pure person
        val table = Table()
        table.name= "pure_person"
        table.view=true
        personMetadata.schema.tables.add(table)
        val colSSN = Column("SSN", "id.SSN", "dbname.SSN")
        colSSN.nullable=false
        table.columns.add(colSSN)

        // person with only telephone
        val tableWithT = Table()
        tableWithT.view=true
        tableWithT.name= "person_with_T"
        personMetadata.schema.tables.add(tableWithT)
        val colT = Column("T", "id.T", "dbname.T")
        colT.nullable=false
        tableWithT.columns.add(colSSN)
        tableWithT.columns.add(colT)

        // person with only S
        val tableWithS = Table()
        tableWithS.view=true
        tableWithS.name= "person_with_S"
        personMetadata.schema.tables.add(tableWithS)
        val colS = Column("S", "id.S", "dbname.S")
        colS.nullable=false
        tableWithS.columns.add(colSSN)
        tableWithS.columns.add(colS)


        // person with only S
        val tableWithTS = Table()
        tableWithTS.view=true
        tableWithTS.name= "person_with_S"
        personMetadata.schema.tables.add(tableWithTS)
        tableWithTS.columns.add(colSSN)
        tableWithTS.columns.add(colT)
        tableWithTS.columns.add(colS)

        // removes table person
        val personTable = personMetadata.schema.table("person")
        personMetadata.schema.tables.remove(personTable)

        // removes constraints from/to person
        var contraintsToRemove = mutableSetOf<Constraint>()
        for (contraint in personMetadata.schema.constraints) {
            if (contraint.source.table=="person") {
                contraintsToRemove.add(contraint)
            }
            if (contraint.target.table=="person") {
                contraintsToRemove.add(contraint)
            }
        }
        personMetadata.schema.constraints.removeAll(contraintsToRemove)

        // add constraint partition
        return UseCaseResult("done apply", personMetadata)
    }
}