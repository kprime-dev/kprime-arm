package unibz.cs.semint.kprime.domain

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "database")
open class Database () {
    @JacksonXmlProperty(isAttribute = true)
    var name: String =""
    @JacksonXmlProperty(isAttribute = true)
    var id: String=""

    var schema: Schema = Schema()

//    @JsonCreator
//    constructor(name:String, id:String, schema:Schema):this() {
//        this.name=name
//        this.id=id
//        this.schema=schema
//    }
}
