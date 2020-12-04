package unibz.cs.semint.kprime.usecase.common

import liquibase.change.core.RawSQLChange
import liquibase.database.Database
import net.sf.jsqlparser.parser.CCJSqlParserManager
import net.sf.jsqlparser.parser.CCJSqlParserUtil
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.StatementVisitor
import net.sf.jsqlparser.statement.select.PlainSelect
import net.sf.jsqlparser.statement.select.SelectVisitorAdapter
import net.sf.jsqlparser.util.TablesNamesFinder
import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.dql.*
import java.io.StringReader

class UnSQLizeSelectUseCase {

    fun fromsql2(queryname: String, sqlquery : String):Query {
        val query = Query()
        query.name = queryname
        val parserManager = CCJSqlParserManager();
        val stmt = parserManager.parse(StringReader(sqlquery)) as net.sf.jsqlparser.statement.select.Select;
        val plainSelect = stmt.selectBody as PlainSelect
        query.select.from = From((plainSelect.fromItem as Table).name)
        query.select.attributes = (plainSelect.selectItems as List<net.sf.jsqlparser.schema.Column>).map { c -> Attribute(c.columnName) }.toMutableList()
        query.select.where = Where(plainSelect.where.toString())
        return query
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
            select.from = From(split[0].trim())
        }
    }
    private fun parseWhere(select: Select, sqlline: String) {
        if (sqlline.startsWith("WHERE ")) {
            val condition = sqlline.drop(6)
            select.where.condition = condition.trim()
        }
    }

}