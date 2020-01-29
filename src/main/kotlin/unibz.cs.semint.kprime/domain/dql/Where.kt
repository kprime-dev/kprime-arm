package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class Where {
    @JacksonXmlProperty(isAttribute = true)
    var condition=String()
}