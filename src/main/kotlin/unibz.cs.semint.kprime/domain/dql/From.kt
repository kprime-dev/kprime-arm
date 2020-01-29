package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class From {
    @JacksonXmlProperty(isAttribute = true)
    var tableName=String()
    @JacksonXmlProperty(isAttribute = true)
    var alias=String()
    @JacksonXmlProperty(isAttribute = true)
    var joinOn=String()
}