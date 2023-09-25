package unibz.cs.semint.kprime.usecase.common

import net.sf.jsqlparser.parser.CCJSqlParserManager
import net.sf.jsqlparser.schema.Table
import net.sf.jsqlparser.statement.select.PlainSelect
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
        val lines = splitSqlToLines(sqlquery).map { line -> uppercaseSqlKeyWords(line) }
        for (line in lines){
            val select = query.select
            val lineStrippedOption = stripOptions(query,line)
            parseSelect(select,lineStrippedOption)
            parseFrom(select,lineStrippedOption)
            parseJoin(select,lineStrippedOption)
            parseJoinOn(select,lineStrippedOption)
            parseWhere(select,lineStrippedOption)
            parseUnionMinus(query,lineStrippedOption)
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

    internal fun stripOptions(query: Query, line: String): String {
        val tokens = line.split(" ")
        var lineStrippedOption = ""
        val options = mutableListOf<String>()
        for (token in tokens) {
            if (token.startsWith("-") && token.length>1) {
                options.add(token)
            } else {
                lineStrippedOption += " $token"
            }
        }
        query.options = options
        return lineStrippedOption.trim()
    }

    private fun uppercaseSqlKeyWords(sqlquery: String): String {
        return sqlquery.split(" ")
                .map { s->s.trim() }
                .map { s-> uppercaseKey(s) }
                .joinToString(" ")
    }
    private  val sqlKeywords = listOf(
            "select","as","from","where","union","minus","join","on"
    )

    private fun uppercaseKey(key: String):String {
        if (sqlKeywords.contains(key.trim())) return key.toUpperCase()
        return key
    }

    private fun splitSqlToLines(sqlquery: String): List<String> {
        val lines = sqlquery.split(System.lineSeparator())
        return lines.flatMap { line->splitOnKeyWords(line) }
    }

    private  val sqlKeywordsLineSeparators = listOf(
            "SELECT","FROM","WHERE","UNION","MINUS"," JOIN "," ON ",
            "select","from","where","union","minus"," join "," on "
    )

    internal fun splitOnKeyWords(line:String): List<String> {
        val result = mutableListOf<String>()
        var token = line
        do {
            var found = false
            for (keyword in sqlKeywordsLineSeparators) {
                val indexOfKeyWord = token.indexOf(keyword)
                if (indexOfKeyWord >1) {
                    result.add(token.substring(0,indexOfKeyWord).trim())
                    token = token.substring(indexOfKeyWord)
                    found = true
                }
            }
        } while(found)
        result.add(token.trim())
//        result.forEach { println(it) }
        return result
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
        lateinit var attributeNames : List<String>
        if (sqlline.startsWith("SELECT DISTINCT ")) {
            attributeNames = sqlline.drop(16).split(",")
            select.distinct = true
        }
        else if (sqlline.startsWith("SELECT ")) {
            attributeNames = sqlline.drop(7).split(",")
        }
        else return
        select.attributes= attributeNames.map { aname -> parseAttribute(aname) }.toCollection(ArrayList<Attribute>())
    }

    private fun parseAttribute(sqlAttribute: String):Attribute {
        val indexOfAS = sqlAttribute.indexOf("AS ")
        return if (indexOfAS>0) {
            val tokens = sqlAttribute.trim().split(" ")
            val name = tokens[0].trim()
            val asName = tokens[2].trim()
            Attribute(name, asName)
        } else {
            val name = sqlAttribute.trim()
            Attribute(name, "")
        }
    }

    private fun parseFrom(select: Select, sqlline: String) {
        if (sqlline.startsWith("FROM ")) {
            val split = sqlline.drop(5).split("AS")
            select.from = From(split[0].trim())
            if (split.size>1)
                select.from.alias = split[1].trim()
        }
    }

    private fun parseJoin(select: Select, sqlline: String) {
        if (sqlline.startsWith("JOIN ")) {
            val split = sqlline.drop(5).split("AS")
            val join = Join()
            join.joinLeftTableAlias = select.from.tableName
            if (select.from.alias?.isNotEmpty()?:false)
                join.joinLeftTableAlias = select.from.alias
            join.joinRightTable = split[0].trim()
            if (split.size>1)
                join.joinRightTableAlias = split[1].trim()
            select.from.addJoin(join)
        }
    }

    private fun parseJoinOn(select: Select, sqlline: String) {
        if (sqlline.startsWith("ON ")) {
            val split = sqlline.drop(3).split("=")
            val join = select.from.joins?.last()
            if (join!=null) {
                join.joinOnLeft = split[0].trim()
                join.joinOnRight = split[1].trim()
            }
        }
    }

    private fun parseWhere(select: Select, sqlline: String) {
        if (sqlline.startsWith("WHERE ")) {
            val condition = sqlline.drop(6)
            select.where.condition = condition.trim()
        }
    }

}