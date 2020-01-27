package unibz.cs.semint.kprime.domain.dml

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText

class CreateView() {

    @JacksonXmlProperty(isAttribute = true)
    var path: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var schemaName: String = ""
    @JacksonXmlProperty(isAttribute = true)
    var viewName: String = ""
    @JacksonXmlText
    var text:String = ""
}