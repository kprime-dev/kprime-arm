package unibz.cs.semint.kprime.domain.dql

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

class Union {

    //JacksonXmlElementWrapper(useWrapping=false)
    var selects: MutableList<Select>? = ArrayList<Select>()

    fun selects(): MutableList<Select> {
        if (selects!=null) return selects as MutableList<Select>
        return ArrayList<Select>()

    }
}