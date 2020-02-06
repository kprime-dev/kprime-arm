package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dql.Query
import unibz.cs.semint.kprime.domain.dql.Select

class SQLizeUseCase {

    /**
     * Given a DB will return SQL commands to create VIEWS that DB.
     */
    fun sqlize(db: Database): List<String> {
        var sqlCommands = mutableListOf<String>()
        for (table in db.schema.tables) {
            if (table.view.isNotEmpty()) {
                val list = table.columns.map { c -> c.name }.toSet().toList().joinToString(",")
                var command = """
                    CREATE OR REPLACE VIEW public.${table.name} AS
                    SELECT ${list}
                    FROM public.${table.view} 
                """
                if (table.condition.isNotEmpty())
                    command += System.lineSeparator()+"WHERE ${table.condition}"
                sqlCommands.add(command.trimIndent())
            }
        }
        return sqlCommands
    }

    fun sqlize(query: Query):String {
        var sql = ""
        sql += sqlize(query.select)
        for (union in query.union) {
            sql +=  "UNION" + System.lineSeparator()
            for (select in union.selects) {
                sql += sqlize(select)
            }
        }
        for (minus in query.minus) {
            sql +=  "MINUS" + System.lineSeparator()
            for (select in minus.selects) {
                sql += sqlize(select)
            }
        }
        return sql
    }

    fun sqlize(select : Select):String {
        var sql = ""
        sql += "SELECT " + select.attributes
                .map { a -> a.name }.toList().joinToString(",") + System.lineSeparator()
        sql += "FROM "
        for (from in select.from) {
            if (from.joinOn.isEmpty()) {
                sql += "  ${from.tableName}"
                if (!from.alias.isEmpty()) sql += " AS ${from.alias}"
                sql += System.lineSeparator()
            }
            else {
                if (from.alias.isEmpty())
                    sql += "  JOIN ${from.tableName} ON ${from.joinOn}"
                else
                    sql += "JOIN ${from.tableName} AS ${from.alias} ON ${from.joinOn}"
            }
        }
        if (!select.where.condition.isEmpty()) {
            sql += "WHERE ${select.where.condition}"  + System.lineSeparator()
        }
        return sql
    }
}