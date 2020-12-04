package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class Where() {

    constructor(condition:String):this(){
        this.condition = condition
    }
    @JacksonXmlProperty(isAttribute = true)
    var condition=String()
}