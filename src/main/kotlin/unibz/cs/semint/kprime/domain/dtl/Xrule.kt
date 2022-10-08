package unibz.cs.semint.kprime.domain.dtl

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

        fun toListOfString(xrules: ArrayList<Xrule>): List<String> {
            val pros = mutableListOf<String>()
            for (xrule in xrules) {
                pros.add("${xrule.name}=${xrule.rule}")
            }
            return pros
        }

    }
}