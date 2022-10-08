package unibz.cs.semint.kprime.domain.dtl

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import unibz.cs.semint.kprime.domain.dtl.Template
import unibz.cs.semint.kprime.domain.dtl.Xmen


@JacksonXmlRootElement(localName = "splitter")
class Splitter {

    var xman = Xmen()
    var template = Template()
}
