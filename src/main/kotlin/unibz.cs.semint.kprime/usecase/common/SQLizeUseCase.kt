package unibz.cs.semint.kprime.usecase.common

import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dql.*

class SQLizeUseCase {

    /**
     * Given a DB will return SQL commands to create VIEWS that DB.
     */
    fun sqlize(db: Database): List<String> {
        var sqlCommands = mutableListOf<String>()
        for (table in db.schema.tables()) {
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
        val union = query.union
        if (union.selects.size>0) {
            for (select in union.selects) {
                sql += System.lineSeparator() + "UNION" + System.lineSeparator()
                sql += sqlize(select)
            }
        }

        val minus = query.minus
        if (minus.selects.size>0) {
            for (select in minus.selects) {
                sql += System.lineSeparator() + "MINUS" + System.lineSeparator()
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
            sql += "WHERE ${select.where.condition}"  //+ System.lineSeparator()
        }
        return sql
    }

    fun fromsql(queryname: String, sqlquery : String):Query {
        val query = Query()
        query.name = queryname
        val lines = sqlquery.split(System.lineSeparator())
        for (line in lines){
            val select = query.select
            parseSelect(select,line)
            parseFrom(select,line)
            parseWhere(select,line)
            parseUnionMinus(query,line)
        }
        if (query.union.selects.size>0) {
            val tmp = query.select
            query.select=query.union.selects.removeAt(0)
            query.union.selects.add(tmp)
        }
        if (query.minus.selects.size>0) {
            val tmp = query.select
            query.select=query.minus.selects.removeAt(0)
            query.minus.selects.add(tmp)
        }
        return query
    }

    private fun parseUnionMinus(query: Query, sqlline: String) {
        if (sqlline.startsWith("UNION")) {
            query.union.selects.add(query.select)
            query.select= Select()
        } else
        if (sqlline.startsWith("MINUS")) {
            query.minus.selects.add(query.select)
            query.select= Select()
        }
    }

    private fun parseSelect(select: Select, sqlline: String) {
        if (sqlline.startsWith("SELECT ")) {
            val split = sqlline.drop(7).split(",")
            select.attributes= split.map { aname -> var a = Attribute(); a.name=aname.trim(); a }.toCollection(ArrayList<Attribute>())
        }
    }
    private fun parseFrom(select: Select, sqlline: String) {
        if (sqlline.startsWith("FROM ")) {
            val split = sqlline.drop(5).split(",")
            select.from = split.map { aname -> var a = From(); a.tableName=aname.trim(); a }.toCollection(ArrayList<From>())
        }
    }
    private fun parseWhere(select: Select, sqlline: String) {
        if (sqlline.startsWith("WHERE ")) {
            val condition = sqlline.drop(6)
            select.where.condition = condition.trim()
        }
    }

}