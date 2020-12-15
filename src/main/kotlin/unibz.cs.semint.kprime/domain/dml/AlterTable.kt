package unibz.cs.semint.kprime.domain.dml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText

@JacksonXmlRootElement(localName = "altertable")
class AlterTable() {

    @JacksonXmlProperty(isAttribute = true)
    var path: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var schemaName: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var tableName: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var statement: String = ""

    infix fun onTable(tableName: String) = apply {
        this.tableName = tableName
    }

    infix fun withPath(path: String ) = apply {
        this.path = path
    }

    infix fun withSchema(schemaName: String) = apply {
        this.schemaName = schemaName
    }

    infix fun withStatement(alterStatement: String) = apply {
        this.statement = alterStatement
    }

}