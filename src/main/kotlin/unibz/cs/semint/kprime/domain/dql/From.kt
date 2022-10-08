package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class From() {

    constructor(name:String): this(){
        tableName = name
    }
    constructor(name:String,alias:String): this(){
        this.tableName = name
        this.alias = alias
    }

    @JacksonXmlProperty(isAttribute = true)
    var tableName=String()
    @JacksonXmlProperty(isAttribute = true)
    var alias=String()

    var joins : ArrayList<Join> ? = null

    fun addJoin(join: Join ) {
        if (joins==null) joins = ArrayList<Join>()
        joins!!.add(join)
    }

    infix fun withName(name:String) {
        this.tableName = name
    }
}