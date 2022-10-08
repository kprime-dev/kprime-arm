package unibz.cs.semint.kprime.domain.ddl

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty


class DropMapping {

    @JacksonXmlProperty(isAttribute = true)
    var name :String = ""

    infix fun withName(newname: String) = apply {
        this.name = newname
    }

}