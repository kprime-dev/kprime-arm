package unibz.cs.semint.kprime.usecase.common

import unibz.cs.semint.kprime.domain.dql.*

class SQLizeSelectUseCase {


    fun sqlize(query: Query):String {
        var sql = ""
        sql += sqlize(query.select)
        val union = query.safeUnion()
        if (union.selects().size>0) {
            for (select in union.selects()) {
                sql += System.lineSeparator() + "UNION" + System.lineSeparator()
                sql += sqlize(select)
            }
        }

        val minus = query.safeMinus()
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
                .map { a -> "${a.name}" }.toList().joinToString(",") + System.lineSeparator()
        sql += "FROM "
        for (from in select.from) {
                sql += "  ${from.tableName}"
                if (!from.alias.isEmpty()) sql += " AS ${from.alias}"
                sql += System.lineSeparator()
                if (from.joins!=null){
                    for (join in from.joins as ArrayList<Join>) {
                        sql += "${join.joinType} JOIN ${join.joinRightTable}" + System.lineSeparator()
                        sql += "ON ${join.joinLeftTable}.${join.joinOnLeft} = ${join.joinRightTable}.${join.joinOnRight}" + System.lineSeparator()
                    }
                }
        }
        if (!select.where.condition.isEmpty()) {
            sql += "WHERE ${select.where.condition}"  //+ System.lineSeparator()
        }
        sql += if (select.limit!=null) " LIMIT "+select.limit else  " LIMIT 10" //+ System.lineSeparator()
        return sql
    }

}