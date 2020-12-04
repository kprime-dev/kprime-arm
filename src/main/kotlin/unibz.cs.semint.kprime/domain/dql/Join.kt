package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class Join {
    @JacksonXmlProperty(isAttribute = true)
    var joinOnLeft=String()
    @JacksonXmlProperty(isAttribute = true)
    var joinLeftTableAlias=String()
    @JacksonXmlProperty(isAttribute = true)
    var joinType=String()
    @JacksonXmlProperty(isAttribute = true)
    var joinOnRight=String()
    @JacksonXmlProperty(isAttribute = true)
    var joinRightTable=String()
    @JacksonXmlProperty(isAttribute = true)
    var joinRightTableAlias :String? = null

}