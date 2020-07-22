package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class Join {
    @JacksonXmlProperty(isAttribute = true)
    var joinOn=String()
    @JacksonXmlProperty(isAttribute = true)
    var joinTable=String()
    @JacksonXmlProperty(isAttribute = true)
    var joinType=String()

}