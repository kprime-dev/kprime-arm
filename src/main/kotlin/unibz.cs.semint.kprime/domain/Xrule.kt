package unibz.cs.semint.kprime.domain

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText
import java.util.*

@JacksonXmlRootElement(localName = "xrule")
class Xrule() {

    @JacksonXmlProperty(isAttribute = true)
    var name = ""
    @JacksonXmlText
    var rule = ""

    companion object {
        fun toProperties(xrules: ArrayList<Xrule>): Properties {
            var pros = Properties()
            for (xrule in xrules) {
                pros[xrule.name]=xrule.rule
            }
            return pros
        }

    }
}