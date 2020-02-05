package unibz.cs.semint.kprime.domain.ddl

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "database")
open class Database () {

    @JacksonXmlProperty(isAttribute = true)
    var name: String =""
    @JacksonXmlProperty(isAttribute = true)
    var id: String=""

    var schema: Schema = Schema()

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

}
