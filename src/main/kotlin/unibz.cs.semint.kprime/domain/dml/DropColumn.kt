package unibz.cs.semint.kprime.domain.dml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty


class DropColumn {

    @JacksonXmlProperty(isAttribute = true)
    var tableName :String = ""

    @JacksonXmlProperty(isAttribute = true)
    var name :String = ""
}