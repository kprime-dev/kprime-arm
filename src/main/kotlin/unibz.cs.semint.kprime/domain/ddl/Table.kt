package unibz.cs.semint.kprime.domain.ddl

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "table")
class Table () {

    @JacksonXmlProperty(isAttribute = true)
    var name: String =""

    @JacksonXmlProperty(isAttribute = true)
    var id: String=""

    @JacksonXmlProperty(isAttribute = true)
    var view: String =""

    @JacksonXmlProperty(isAttribute = true)
    var condition: String =""

    var columns= ArrayList<Column>()

    fun hasColumn(nameToFind:String): Boolean {
        for (col in columns) {
            if (col.name.equals(nameToFind)) return true
        }
        return false
    }

    fun hasColumns(namesToFind:List<String>) : Boolean {
        for (name in namesToFind) {
            if (!hasColumn(name)) return false
        }
        return true
    }

    fun hasNullable(): Boolean {
        for (col in columns) {
            if (col.nullable) return true
        }
        return false
    }

    infix fun id(id: String ) = apply {
        this.id = id
    }

    infix fun name(name: String ) = apply {
        this.name = name
    }

    infix fun withColumn(name: String ) = apply {
        val col = Column()
        col.name = name
        this.columns.add(col)
    }


}
