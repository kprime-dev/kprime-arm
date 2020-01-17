package unibz.cs.semint.kprime.domain

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty

class ChangeSet() {

    @JacksonXmlProperty(isAttribute = true)
    var id: String = ""

    @JacksonXmlElementWrapper(useWrapping=false)
    var createView= ArrayList<CreateView>()

}