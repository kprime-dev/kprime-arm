package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "query")
class Query {
    var select = Select()
    @JacksonXmlElementWrapper(useWrapping=false)
    var union= ArrayList<Union>()
    @JacksonXmlElementWrapper(useWrapping=false)
    var minus= ArrayList<Minus>()
}