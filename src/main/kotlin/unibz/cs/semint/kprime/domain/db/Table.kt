package unibz.cs.semint.kprime.domain.db

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import unibz.cs.semint.kprime.domain.label.Labelled
import unibz.cs.semint.kprime.domain.label.Labeller

@JacksonXmlRootElement(localName = "table")
class Table (): Labelled by Labeller() {

    @JacksonXmlProperty(isAttribute = true)
    var name: String =""

    @JacksonXmlProperty(isAttribute = true)
    var id: String=""

    @JacksonXmlProperty(isAttribute = true)
    var view: String =""

    @JacksonXmlProperty(isAttribute = true)
    var condition: String =""

    @JacksonXmlProperty(isAttribute = true)
    var parent: String? = null

    @JacksonXmlElementWrapper(localName = "columns")
    @JacksonXmlProperty(localName = "column")
    var columns= ArrayList<Column>()

    @JacksonXmlProperty(isAttribute = true)
    var catalog: String? = null

    @JacksonXmlProperty(isAttribute = true)
    var schema: String? = null

    @JacksonXmlProperty(isAttribute = true)
    var source: String? = null

    @JacksonXmlProperty(isAttribute = true)
    var labels: String? = null
        get() = if (labelsAsString().isEmpty()) null else labelsAsString()
        set(value) { field = resetLabels(value?:"") }

    @JacksonXmlProperty(isAttribute = true)
    var `var`: String? = null

    @JacksonXmlProperty(isAttribute = true)
    var primaryKey: String? = null

    @JacksonXmlProperty(isAttribute = true)
    var naturalKey: String? = null

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

    fun colByName(colName:String):Column? {
        return columns?.filter { c-> c.name==colName }?.firstOrNull()
    }

    infix fun id(id: String ) = apply {
        this.id = id
    }

    infix fun name(name: String ) = apply {
        this.name = name
    }

    infix fun withColumn(name: String ) = apply {
        val attName = name.substringAfterLast(":")
        val type = name.substringBeforeLast(":")
        val col = Column.of(attName)
        col.type = type
        this.columns.add(col)
    }

    infix fun withCols(cols: Set<Column>) = apply {
        this.columns.addAll(cols)
    }

    fun addColomunsLabels(labelsAsString: String): String {
        for (column in columns) column.addLabels(labelsAsString)
        return ""
    }

    override fun toString(): String {
        return "Table(name='$name', id='$id', view='$view', condition='$condition', parent=$parent, columns=$columns, catalog=$catalog, schema=$schema, source=$source)"
    }


}
