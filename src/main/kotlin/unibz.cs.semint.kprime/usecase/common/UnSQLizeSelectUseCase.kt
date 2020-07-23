package unibz.cs.semint.kprime.usecase.common

import unibz.cs.semint.kprime.domain.dql.*

class UnSQLizeSelectUseCase {

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
        if (query.safeUnion().selects().size>0) {
            val tmp = query.select
            query.select=query.safeUnion().selects().removeAt(0)
            query.safeUnion().selects().add(tmp)
        }
        if (query.safeMinus().selects().size>0) {
            val tmp = query.select
            query.select=query.safeMinus().selects().removeAt(0)
            query.safeMinus().selects().add(tmp)
        }
        return query
    }

    private fun parseUnionMinus(query: Query, sqlline: String) {
        if (sqlline.startsWith("UNION")) {
            query.safeUnion().selects().add(query.select)
            query.select= Select()
        } else
        if (sqlline.startsWith("MINUS")) {
            query.safeMinus().selects().add(query.select)
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