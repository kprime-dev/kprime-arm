package unibz.cs.semint.kprime.domain.db

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import unibz.cs.semint.kprime.domain.label.Labelled
import unibz.cs.semint.kprime.domain.label.Labeller

@JacksonXmlRootElement(localName = "constraint")
class Constraint : Labelled by Labeller() {
    fun left(): Collection<Column> {
        return source.columns
    }

    fun right(): Collection<Column> {
        return target.columns
    }

    fun withName(newname:String) {
        this.name=newname
    }
    enum class TYPE {
        FOREIGN_KEY,
        PRIMARY_KEY,
        ID_KEY,
        CANDIDATE_KEY,
        SURROGATE_KEY,
        FUNCTIONAL,
        MULTIVALUED,
        DOUBLE_INCLUSION,
        INCLUSION,
        PARTITION,
        DISJUNCTION,
        COVER,
        NOTNULL,
        UNIQUE,
        OR_AT_LEAST_ONE,
        SUBSET
    }
    @JacksonXmlProperty(isAttribute = true)
    var name: String =""
    @JacksonXmlProperty(isAttribute = true)
    var id: String=""
    @JacksonXmlProperty(isAttribute = true)
    var type: String=""

    var source = Source()
    @JacksonXmlProperty(isAttribute = false)
    @JacksonXmlElementWrapper(useWrapping = false)
    var target = Target()

    @JacksonXmlProperty(isAttribute = true)
    var labels: String? = null
        get() = if (labelsAsString().isEmpty()) null else labelsAsString()
        set(value) { field = resetLabels(value?:"") }

    fun clone():Constraint {
        val objectMapper = ObjectMapper()
        val asString = objectMapper.writeValueAsString(this)
        return objectMapper.readValue(asString,Constraint::class.java)
    }

    fun hasTypeKey():Boolean {
        return type == TYPE.PRIMARY_KEY.name
    }

    fun hasTypeFunctional():Boolean {
        return type == TYPE.FUNCTIONAL.name
    }


    companion object {

        fun addKey():Constraint {
            val constraint = Constraint()
            constraint.type = TYPE.PRIMARY_KEY.name
            return constraint
        }

        fun foreignkey():Constraint {
            val constraint = Constraint()
            constraint.type = TYPE.FOREIGN_KEY.name
            return constraint
        }

        fun addFunctional():Constraint {
            val constraint = Constraint()
            constraint.type = TYPE.FUNCTIONAL.name
            return constraint
        }

        fun inclusion():Constraint {
            val constraint = Constraint()
            constraint.type = TYPE.INCLUSION.name
            return constraint
        }

        fun doubleInclusion():Constraint {
            val constraint = Constraint()
            constraint.type = TYPE.DOUBLE_INCLUSION.name
            return constraint
        }


        fun set(exprs : String ):Set<Constraint> {
            if (exprs == "") return HashSet()
            val replace = exprs.replace("\\s+", "") // TODO verify
            return set(exprs.split(";"))
        }

        fun set(exprs : List<String>):Set<Constraint> {
            val fds = HashSet<Constraint>()
            for (s in exprs) {
                fds.add(of(s))
            }
            return fds
        }

        fun of(expr:String):Constraint {
            val split = expr.split("-->")
            return of(split[0],split[1])
        }

        fun of(left:String, right:String,type:String=""):Constraint {
            val left1 = left.replace("\\s+","")
            val right1 = right.replace("\\s+","")
            val lefts = left1.split(",")
            val rights = right1.split(",")
            val c = Constraint()
            c.source.columns = cols(lefts)
            c.target.columns = cols(rights)
            c.type = type
            return c
        }

        fun of(left:Collection<Column>, right:Collection<Column>,type:String=""):Constraint {
            val c = Constraint()
            c.source.columns.addAll(left)
            c.target.columns.addAll(right)
            c.type = type
            return c
        }

        private fun cols(names:List<String>):ArrayList<Column> {
            val result = ArrayList<Column>()
            for (name in names) {
                val c = Column()
                c.name = name.trim()
                result.add(c)
            }
            return result
        }
    }

    fun toStringWithName(): String {
        return "${this.name}:${toString()}"
    }

    override fun toString(): String {
        if (source.columns.isEmpty()) return "no source columns"
        if (right()==null) return "no target columns"
        var result = type +" " +source.table+":"+source.columns[0].toString()
        for(col in source.columns.drop(1))
            result += ",$col"
        result +=" --> " +target.table+":"
        if (target.columns.size>0) {
            result+= target.columns[0].toString()
            for (col in target.columns.drop(1))
                result += ",$col"
        }
        result +=" ; "
        return result
    }

    override fun equals(other: Any?): Boolean {
        return this.toString() == (other as Constraint).toString()
    }

    override fun hashCode(): Int {
        return this.toString().hashCode()
    }

}
