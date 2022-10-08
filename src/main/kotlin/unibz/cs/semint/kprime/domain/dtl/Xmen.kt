package unibz.cs.semint.kprime.domain.dtl

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "xmen")
class Xmen {
    var xrules = ArrayList<Xrule>()
}