package unibz.cs.semint.kprime.domain.dtl

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "transformer")
class Transformer {
    @JacksonXmlProperty(isAttribute = true)
    var name: String =""

    var composer = Composer()
    var splitter = Splitter()
}