package unibz.cs.semint.kprime.domain.ddl

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

fun key(alfa:Constraint.()->Unit):Constraint {
    val constraint = Constraint()
    constraint.type = Constraint.TYPE.PRIMARY_KEY.name
    return constraint
}

fun foreignkey(alfa:Constraint.()->Unit):Constraint {
    val constraint = Constraint()
    constraint.type = Constraint.TYPE.FOREIGN_KEY.name
    return constraint
}

fun functional(alfa:Constraint.()->Unit):Constraint {
    val constraint = Constraint()
    constraint.type = Constraint.TYPE.FUNCTIONAL.name
    return constraint
}

fun inclusion(alfa:Constraint.()->Unit):Constraint {
    val constraint = Constraint()
    constraint.type = Constraint.TYPE.INCLUSION.name
    return constraint
}

fun doubleInclusion(alfa:Constraint.()->Unit):Constraint {
    val constraint = Constraint()
    constraint.type = Constraint.TYPE.DOUBLE_INCLUSION.name
    return constraint
}

@JacksonXmlRootElement(localName = "constraint")
class Constraint () {

    enum class TYPE {
        FOREIGN_KEY,PRIMARY_KEY,FUNCTIONAL,DOUBLE_INCLUSION,INCLUSION,DISJUNCTION,COVER
    }
    @JacksonXmlProperty(isAttribute = true)
    var name: String =""
    @JacksonXmlProperty(isAttribute = true)
    var id: String=""
    @JacksonXmlProperty(isAttribute = true)
    var type: String=""

    var source = Source()
    var target = Target()

}
