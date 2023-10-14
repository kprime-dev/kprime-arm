package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "select")
@JsonPropertyOrder("distinct", "attributes", "from", "where", "groupBy", "limit")
class Select {

    var distinct: Boolean = false
    @JacksonXmlElementWrapper(localName = "attributes")
    @JacksonXmlProperty(localName = "attribute")
    var attributes : MutableList<Attribute> = ArrayList<Attribute>()
    var from = From()
    var where = Where()
    var groupBy : MutableList<Attribute> = ArrayList<Attribute>()
    @JacksonXmlProperty(isAttribute = true)
    var limit : String? = null

    fun addAttributes(args: List<String>) {
        for (arg in args) {
            val att = Attribute()
            att.name = arg
            attributes.add(att)
        }
    }

    fun addGroupBy(args: List<String>) {
        for (arg in args) {
            val att = Attribute()
            att.name = arg
            groupBy.add(att)
        }
    }

    override fun toString(): String {
        return "Select(distinct=$distinct, attributes=$attributes, from=$from, where=$where, groupBy=$groupBy, limit=$limit)"
    }

}