package unibz.cs.semint.kprime.domain.dml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty


class DropMapping {

    @JacksonXmlProperty(isAttribute = true)
    var name :String = ""
}