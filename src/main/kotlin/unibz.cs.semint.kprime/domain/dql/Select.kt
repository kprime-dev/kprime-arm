package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "select")
class Select {

    var attributes = ArrayList<Attribute>()
    var from = ArrayList<From>()
    var where = Where()
    @JacksonXmlProperty(isAttribute = true)
    var limit : String? = null

}