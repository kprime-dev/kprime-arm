package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import unibz.cs.semint.kprime.domain.ddl.Column

class From {
    @JacksonXmlProperty(isAttribute = true)
    var tableName=String()
    @JacksonXmlProperty(isAttribute = true)
    var alias=String()

    var joins : ArrayList<Join> ? = null
}