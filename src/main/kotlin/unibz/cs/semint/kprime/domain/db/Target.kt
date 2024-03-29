package unibz.cs.semint.kprime.domain.db

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "target")
class Target () {

    @JacksonXmlProperty(isAttribute = true)
    var name: String =""
    @JacksonXmlProperty(isAttribute = true)
    var id: String=""
    @JacksonXmlProperty(isAttribute = true)
    var table: String=""

    @JacksonXmlElementWrapper(localName = "columns")
    @JacksonXmlProperty(localName = "column")
    var columns = ArrayList<Column>()
}
