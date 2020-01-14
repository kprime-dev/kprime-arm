package unibz.cs.semint.kprime.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class Column () {
    @JacksonXmlProperty(isAttribute = true)
    var name: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var id: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var dbname: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var nullable: Boolean = false
    @JacksonXmlProperty(isAttribute = true)
    var dbtype: String = ""

    @JsonCreator
    constructor(
        @JsonProperty("name")  name: String,
        @JsonProperty("id") id: String,
        @JsonProperty("dbname") dbname: String) : this() {
        this.name= name
        this.id=id
        this.dbname=dbname
    }

}