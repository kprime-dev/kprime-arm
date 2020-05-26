package unibz.cs.semint.kprime.domain.ddl

object SchemaCmdParser {

    fun parseTable(commandArgs: String): Table {
        val tableName: String = commandArgs.split(":")[0]
        val attributes = commandArgs.split(":")[1].split(",")
        val table = Table()
        table.name = tableName
        for (att in attributes) table withColumn att
        return table
    }


}