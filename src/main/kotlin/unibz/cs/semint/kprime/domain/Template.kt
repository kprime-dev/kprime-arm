package unibz.cs.semint.kprime.domain

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.util.*

@JacksonXmlRootElement(localName = "template")
class Template {
    @JacksonXmlProperty(isAttribute = true)
    var filename=""
}