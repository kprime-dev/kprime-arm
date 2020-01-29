package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class Attribute {

    @JacksonXmlProperty(isAttribute = true)
    var name = String()
}