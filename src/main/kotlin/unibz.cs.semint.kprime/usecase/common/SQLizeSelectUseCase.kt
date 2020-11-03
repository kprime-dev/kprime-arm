package unibz.cs.semint.kprime.usecase.common

import unibz.cs.semint.kprime.domain.dql.*

class SQLizeSelectUseCase {


    fun sqlize(query: Query):String {
        var sql = ""
        sql += sqlizeSelect(query.select)
        sql = sqlizeUnion(query, sql)
        sql = sqlizeMinus(query, sql)
        return sql
    }

    private fun sqlizeMinus(query: Query, sql: String): String {
        var sql1 = sql
        val minus = query.safeMinus()
        if (minus.selects().size > 0) {
            for (select in minus.selects()) {
                sql1 += System.lineSeparator() + "MINUS" + System.lineSeparator()
                sql1 += sqlizeSelect(select)
            }
        }
        return sql1
    }

    private fun sqlizeUnion(query: Query, sql: String): String {
        var sql1 = sql
        val union = query.safeUnion()
        if (union.selects().size > 0) {
            for (select in union.selects()) {
                sql1 += System.lineSeparator() + "UNION" + System.lineSeparator()
                sql1 += sqlizeSelect(select)
            }
        }
        return sql1
    }

    fun sqlizeSelect(select : Select):String {
        var sql = ""
        sql += "SELECT " + select.attributes
                .map { a -> "'${a.name}'" }.toList().joinToString(",") + System.lineSeparator()
        sql += "FROM "
        sql = sqlizeFrom(select, sql)
        if (!select.where.condition.isEmpty()) {
            sql += "WHERE ${select.where.condition}"  //+ System.lineSeparator()
        }
        sql += if (select.limit!=null) " LIMIT "+select.limit else  " LIMIT 10" //+ System.lineSeparator()
        return sql
    }

    private fun sqlizeFrom(select: Select, sql: String): String {
        var sql1 = sql
        for (from in select.from) {
            sql1 += "  ${from.tableName}"
            if (!from.alias.isEmpty()) sql1 += " AS ${from.alias}"
            sql1 += System.lineSeparator()
            if (from.joins != null) {
                for (join in from.joins as ArrayList<Join>) {
                    sql1 += "${join.joinType} JOIN ${join.joinRightTable}" + System.lineSeparator()
                    sql1 += "ON ${join.joinLeftTable}.${join.joinOnLeft} = ${join.joinRightTable}.${join.joinOnRight}" + System.lineSeparator()
                }
            }
        }
        return sql1
    }

}