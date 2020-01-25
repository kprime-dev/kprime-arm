package unibz.cs.semint.kprime.domain

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText

@JacksonXmlRootElement(localName = "xrule")
class Xrule() {

    @JacksonXmlProperty(isAttribute = true)
    var name = ""
    @JacksonXmlText
    var rule = ""

}