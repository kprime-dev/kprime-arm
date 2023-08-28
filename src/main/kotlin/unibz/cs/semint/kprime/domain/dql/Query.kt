package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Table

@JacksonXmlRootElement(localName = "query")
open class Query {
    @JacksonXmlProperty(isAttribute = true)
    var id = ""
    @JacksonXmlProperty(isAttribute = true)
    var name = ""
    var select = Select()
    @JacksonXmlElementWrapper(useWrapping=false)
    var union : Union? = null
    @JacksonXmlElementWrapper(useWrapping=false)
    var minus : Minus? = null
    @JacksonXmlElementWrapper(useWrapping=false)
    var options : List<String>? = null

    fun safeMinus():Minus {
        if (minus==null) {
            this.minus = Minus()
        }
        return this.minus as Minus
    }

    fun safeUnion():Union {
        if (union==null) {
            this.union = Union()
        }
        return this.union as Union
    }

    companion object {
        fun build(tableName:String): Query {
            val query = Query()
            var select = query.select
            var attr = Attribute()
            attr.name = "*"
            select.attributes.add(attr)
            val fromT1 = From()
            fromT1.tableName = tableName
            select.from = fromT1
            return query
        }

        fun build(tableName:String, condition:String): Query {
            val query = Query()
            var select = query.select
            var attr = Attribute()
            attr.name = "*"
            select.attributes.add(attr)
            val fromT1 = From()
            fromT1.tableName = tableName
            select.from = fromT1
            select.where.condition = condition
            return query
        }

        fun buildFromTable(table: Table): Query {
            val query = Query()
            var select = query.select
            select.attributes.addAll(table.columns.map { Attribute(it.name) })
            val fromT1 = From()
            fromT1.tableName = table.name
            select.from = fromT1
            return query
        }

        fun buildFromTable(database: Database, tableName:String, condition:String): Query {
            val colNames = database.schema.tables().filter { t -> t.name == tableName }.first().columns.map { c -> var attr = Attribute(); attr.name=c.name; attr }
            val query = Query()
            var select = query.select
            select.attributes.addAll(colNames)
            val fromT1 = From()
            fromT1.tableName = tableName
            select.from = fromT1
            select.where.condition = condition
            return query
        }

        fun buildFromTable(database: Database, tableName:String): Query {
            val colNames = database.schema.tables().filter { t -> t.name == tableName }.first().columns.map { c -> var attr = Attribute(); attr.name=c.name; attr }
            val query = Query()
            var select = query.select
            select.attributes.addAll(colNames)
            val fromT1 = From()
            fromT1.tableName = database.lineage(tableName).last()
            select.from = fromT1
            return query
        }

        fun buildFromMapping(database: Database, tableName:String): Query? {
            return database.mappings().filter { m-> m.name == tableName }.firstOrNull()
        }

    }
}