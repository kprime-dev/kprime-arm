package unibz.cs.semint.kprime.domain.dml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class DropConstraint() {

    @JacksonXmlProperty(isAttribute = true)
    var path: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var schemaName: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var constraintName: String = ""

    infix fun withPath(path: String ) = apply {
        this.path = path
    }

    infix fun withSchema(schemaName: String) = apply {
        this.schemaName = schemaName
    }

    infix fun   name(constraintName: String) = apply {
        this.constraintName = constraintName
    }

}