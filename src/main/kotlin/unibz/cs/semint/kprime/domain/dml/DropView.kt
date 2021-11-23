package unibz.cs.semint.kprime.domain.dml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText

class DropView() {

    @JacksonXmlProperty(isAttribute = true)
    var path: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var schemaName: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var viewName: String = ""

    infix fun withPath(path: String ) = apply {
        this.path = path
    }

    infix fun withSchema(schemaName: String) = apply {
        this.schemaName = schemaName
    }

    infix fun withView(viewName: String) = apply {
        this.viewName = viewName
    }
}