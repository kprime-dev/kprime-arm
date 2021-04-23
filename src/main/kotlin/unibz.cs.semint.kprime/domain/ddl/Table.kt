package unibz.cs.semint.kprime.domain.ddl

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "table")
class Table (): Labelled {

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

    var columns= ArrayList<Column>()

    @JacksonXmlProperty(isAttribute = true)
    var labels: String? = null

    @JacksonXmlProperty(isAttribute = true)
    var catalog: String? = null

    @JacksonXmlProperty(isAttribute = true)
    var schema: String? = null

    @JacksonXmlProperty(isAttribute = true)
    var source: String? = null


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
        val col = Column.of(name)
        //col.name = name
        this.columns.add(col)
    }

    infix fun withCols(cols: Set<Column>) = apply {
        this.columns.addAll(cols)
    }

    override fun resetLabels(labelsAsString: String): String {
        labels = labelsAsString
        return labels!!
    }

    override fun addLabels(labelsAsString: String): String {
        if (labels==null) labels = labelsAsString
        else labels += labelsAsString
        return labels!!
    }

    override fun addLabels(newLabels: List<Label>): String {
        return addLabels(newLabels.joinToString(","))
    }

    override fun hasLabel(label: String): Boolean {
        return labels?.contains(label)?:false
    }

    override fun labelsAsString(): String {
        return labels?: ""
    }

    override fun remLabels(newLabels: List<Label>): String {
        val labels2 = labels ?: return ""
        return resetLabels(labels2.split(",")
                .filter { !newLabels.contains(it) }
                .joinToString(","))
    }

    fun addColomunsLabels(labelsAsString: String): String {
        for (column in columns) column.addLabels(labelsAsString)
        return ""
    }

    override fun toString(): String {
        return "Table(name='$name', id='$id', view='$view', condition='$condition', parent=$parent, columns=$columns, labels=$labels, catalog=$catalog, schema=$schema, source=$source)"
    }


}
