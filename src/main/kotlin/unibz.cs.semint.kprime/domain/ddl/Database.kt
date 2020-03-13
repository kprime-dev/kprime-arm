package unibz.cs.semint.kprime.domain.ddl

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import unibz.cs.semint.kprime.domain.dql.Query
import java.util.*
import javax.xml.bind.annotation.XmlElements
import kotlin.collections.ArrayList

@JacksonXmlRootElement(localName = "database")
open class Database () {

    @JacksonXmlProperty(isAttribute = true)
    var name: String =""
    @JacksonXmlProperty(isAttribute = true)
    var id: String=""

    var schema: Schema = Schema()
    @JacksonXmlElementWrapper(localName = "mappings")
    @JacksonXmlProperty(localName = "query")
    var mappings : MutableList<Query>? = ArrayList<Query>()

    init {
        this.mappings = mutableListOf()
    }

    fun lineage(tableName:String) : List<String> {
            val result = mutableListOf<String>()
            var viewTable = tableName
            while (!viewTable.isEmpty()) {
                result.add(viewTable)
                if (schema.table(viewTable)==null) viewTable = ""
                else viewTable = (schema.table(viewTable) as Table).view
            }
            return result
        }

        fun mappings():MutableList<Query> {
            if (mappings!=null) return mappings as MutableList<Query>
            return ArrayList<Query>()
        }

        fun mapping(name:String): Query? {
            return mappings().filter { m -> m.name.equals(name) }.firstOrNull()
        }
}
