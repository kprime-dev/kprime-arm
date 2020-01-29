package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

class Union {

    //JacksonXmlElementWrapper(useWrapping=false)
    var selects = ArrayList<Select>()
}