package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import unibz.cs.semint.kprime.domain.ddl.Database

@JacksonXmlRootElement(localName = "query")
class Query {
    var select = Select()
    @JacksonXmlElementWrapper(useWrapping=false)
    var union= ArrayList<Union>()
    @JacksonXmlElementWrapper(useWrapping=false)
    var minus= ArrayList<Minus>()

    companion object {
        fun simpleFilmQueryFixture(tableName:String): Query {
            val query = Query()
            var select = query.select
            var attr = Attribute()
            attr.name = "*"
            select.attributes.add(attr)
            val fromT1 = From()
            fromT1.tableName = tableName
            select.from.add(fromT1)
            return query
        }

        fun simpleFilmQueryFixture(tableName:String,condition:String): Query {
            val query = Query()
            var select = query.select
            var attr = Attribute()
            attr.name = "*"
            select.attributes.add(attr)
            val fromT1 = From()
            fromT1.tableName = tableName
            select.from.add(fromT1)
            select.where.condition = condition
            return query
        }

        fun simpleQueryFixture(database: Database, tableName:String, condition:String): Query {
            val colNames = database.schema.tables.filter { t -> t.name == tableName }.first().columns.map { c -> var attr = Attribute(); attr.name=c.name; attr }
            val query = Query()
            var select = query.select
            select.attributes.addAll(colNames)
            val fromT1 = From()
            fromT1.tableName = tableName
            select.from.add(fromT1)
            select.where.condition = condition
            return query
        }

        fun simpleQueryFixture(database: Database, tableName:String): Query {
            val colNames = database.schema.tables.filter { t -> t.name == tableName }.first().columns.map { c -> var attr = Attribute(); attr.name=c.name; attr }
            val query = Query()
            var select = query.select
            select.attributes.addAll(colNames)
            val fromT1 = From()
            fromT1.tableName = database.lineage(tableName).last()
            select.from.add(fromT1)
            return query
        }


    }
}