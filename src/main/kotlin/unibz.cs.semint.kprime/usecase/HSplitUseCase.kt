package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.*
import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Constraint
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.ddl.Table

class HSplitUseCase {

    fun compute(databaseMetadata: Database) {
        printDb(databaseMetadata)
        val  detected = detect(databaseMetadata)
        if (detected.ok!=null) {
            val applied = apply(databaseMetadata, detected)
            if (applied.ok!=null) {
                printDb(applied.ok)
                printSql(SQLizeUseCase().sqlize(applied.ok))
            }
        }
    }

    private fun printSql(sqlines: List<String>) {
        println()
        println("--------------------------------------------------------------------------")
        for (sql in sqlines) println(sql)
    }

    private fun printDb(db: Database) {
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
        table.view="person"
        table.condition="person.T=null AND person.S=null"
        personMetadata.schema.tables.add(table)
        val colSSN = Column("SSN", "id.SSN", "dbname.SSN")
        colSSN.nullable=false
        table.columns.add(colSSN)

        // person with only telephone
        val tableWithT = Table()
        tableWithT.view="person"
        tableWithT.name= "person_with_T"
        tableWithT.condition="person.T NOT null AND person.S=null"
        personMetadata.schema.tables.add(tableWithT)
        val colT = Column("T", "id.T", "dbname.T")
        colT.nullable=false
        tableWithT.columns.add(colSSN)
        tableWithT.columns.add(colT)

        // person with only TS
        val tableWithS = Table()
        tableWithS.view="person"
        tableWithS.name= "person_with_TS"
        tableWithS.condition="person.T = null AND person.S NOT null"
        personMetadata.schema.tables.add(tableWithS)
        val colS = Column("S", "id.S", "dbname.S")
        colS.nullable=false
        tableWithS.columns.add(colSSN)
        tableWithS.columns.add(colS)


        // person with only S
        val tableWithTS = Table()
        tableWithTS.view="person"
        tableWithTS.name= "person_with_S"
        tableWithTS.condition="person.T NOT null AND person.S NOT null"
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