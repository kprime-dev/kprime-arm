package unibz.cs.semint.kprime.domain.ddl

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "droptable")
class DropTable() {

    @JacksonXmlProperty(isAttribute = true)
    var cascadeConstraints: Boolean? = null

    @JacksonXmlProperty(isAttribute = true)
    var catalog: String? = null

    @JacksonXmlProperty(isAttribute = true)
    var path: String = ""

    @JacksonXmlProperty(isAttribute = true)
    var schemaName: String = ""

    @JacksonXmlProperty(isAttribute = true)
    var tableName: String = ""

    infix fun withPath(path: String ) = apply {
        this.path = path
    }

    infix fun withSchema(schemaName: String) = apply {
        this.schemaName = schemaName
    }

    infix fun name(viewName: String) = apply {
        this.tableName = viewName
    }

}