package unibz.cs.semint.kprime.domain.ddl

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import kotlin.math.exp

class Column () {
    @JacksonXmlProperty(isAttribute = true)
    var name: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var id: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var dbname: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var nullable: Boolean = false
    @JacksonXmlProperty(isAttribute = true)
    var dbtype: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var type: String? = null
    @JacksonXmlProperty(isAttribute = true)
    var unit: String? = null
    @JacksonXmlProperty(isAttribute = true)
    var cardinality: String? = null
    @JacksonXmlProperty(isAttribute = true)
    var role: String? = null

    @JsonCreator
    constructor(
        @JsonProperty("name")  name: String,
        @JsonProperty("id") id: String,
        @JsonProperty("dbname") dbname: String) : this() {
        this.name= name
        this.id=id
        this.dbname=dbname
    }

    companion object {
        fun set(expr:String):Set<Column> {
            if (expr.isEmpty()) return HashSet<Column>()
            val names = expr.replace("\\s+","")
            return set(names.split(","))
        }

        fun set(names: List<String>): HashSet<Column> {
            val attrs = HashSet<Column>()
            for (name in names) {
                attrs.add(of(name))
            }
            return attrs
        }

        fun of(name:String):Column {
            val c = Column()
            c.name = name.trim()
            return c

        }

    }
    override fun toString(): String {
        return "$name"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Column
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }


}