package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class Attribute() {

    constructor(name:String) :this() {
        this.name = name
    }

    constructor(name:String,asName:String) :this() {
        this.name = name
        this.asName = asName
    }
    @JacksonXmlProperty(isAttribute = true)
    var name = String()
    @JacksonXmlProperty(isAttribute = true)
    var asName : String? = null

    override fun equals(other: Any?): Boolean {
        if (other == null) return false;
        return if (other is Attribute)
            name.equals(other.name)
        else false
    }
}