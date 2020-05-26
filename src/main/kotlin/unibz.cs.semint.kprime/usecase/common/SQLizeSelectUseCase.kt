package unibz.cs.semint.kprime.usecase.common

import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dql.*

class SQLizeSelectUseCase {


    fun sqlize(query: Query):String {
        var sql = ""
        sql += sqlize(query.select)
        val union = query.union
        if (union.selects().size>0) {
            for (select in union.selects()) {
                sql += System.lineSeparator() + "UNION" + System.lineSeparator()
                sql += sqlize(select)
            }
        }

        val minus = query.minus
        if (minus.selects().size>0) {
            for (select in minus.selects()) {
                sql += System.lineSeparator() + "MINUS" + System.lineSeparator()
                sql += sqlize(select)
            }
        }

        return sql
    }

    fun sqlize(select : Select):String {
        var sql = ""
        sql += "SELECT " + select.attributes
                .map { a -> "\"${a.name}\"" }.toList().joinToString(",") + System.lineSeparator()
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

}