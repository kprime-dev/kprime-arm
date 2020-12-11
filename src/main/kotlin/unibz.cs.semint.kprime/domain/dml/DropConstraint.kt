package unibz.cs.semint.kprime.domain.dml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class DropConstraint() {

    @JacksonXmlProperty(isAttribute = true)
    var path: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var schemaName: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var constraintName: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var type: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var tableName: String = ""

    infix fun withPath(path: String ) = apply {
        this.path = path
    }

    infix fun withSchema(schemaName: String) = apply {
        this.schemaName = schemaName
    }

    infix fun name(constraintName: String) = apply {
        this.constraintName = constraintName
    }

    infix fun type(type:String) = apply {
        this.type = type
    }

    infix fun table(table:String) = apply {
        this.tableName = table
    }
}