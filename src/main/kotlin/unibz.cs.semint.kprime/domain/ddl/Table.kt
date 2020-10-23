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

    private var labeller = Labeller()

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

    infix fun withCols(cols: Set<Column>) = apply {
        this.columns.addAll(cols)
    }

    override fun resetLabels(labelsAsString: String): String {
        labels = labeller.resetLabels(labelsAsString)
        return labelsAsString()
    }

    override fun addLabels(labelsAsString: String): String {
        labels = labeller.addLabels(labelsAsString)
        return labelsAsString()
    }

    fun addColomunsLabels(labelsAsString: String): String {
        for (column in columns) column.addLabels(labelsAsString)
        return ""
    }

    override fun addLabels(newLabels: List<Label>): String {
        labels = labeller.addLabels(newLabels)
        return labelsAsString()
    }

    override fun labelsAsString(): String {
        return labels?: ""
    }

    override fun hasLabel(label: String): Boolean {
        return labeller.hasLabel(label)
    }

}
