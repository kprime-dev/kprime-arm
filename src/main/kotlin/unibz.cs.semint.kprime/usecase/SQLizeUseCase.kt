package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.ddl.Database

class SQLizeUseCase {

    /**
     * Given a DB will return SQL commands to create that DB.
     */
    fun sqlize(db: Database): List<String> {
        var sqlCommands = mutableListOf<String>()
        for (table in db.schema.tables) {
            if (table.view.isNotEmpty()) {
                sqlCommands.add("""
                    CREATE VIEW ${table.name}
                    SELECT ${table.columns[0].name}
                    FROM ${table.view}
                    WHERE ${table.condition}
                """.trimIndent())
            }
        }
        return sqlCommands
    }
}