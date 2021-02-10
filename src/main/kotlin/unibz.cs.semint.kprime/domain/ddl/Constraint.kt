package unibz.cs.semint.kprime.domain.ddl

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "constraint")
class Constraint(): Labelled {
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
        CANDIDATE_KEY,
        SURROGATE_KEY,
        FUNCTIONAL,
        MULTIVALUED,
        DOUBLE_INCLUSION,
        INCLUSION,
        PARTITION,
        DISJUNCTION,
        COVER
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

    fun clone():Constraint {
        val objectMapper = ObjectMapper()
        val asString = objectMapper.writeValueAsString(this)
        return objectMapper.readValue(asString,Constraint::class.java)
    }

    fun hasTypeKey():Boolean {
        return type!=null && type.equals(TYPE.PRIMARY_KEY.name)
    }

    fun hasTypeFunctional():Boolean {
        return type!=null && type.equals(TYPE.FUNCTIONAL.name)
    }


    companion object {

        fun addKey(alfa:Constraint.()->Unit):Constraint {
            val constraint = Constraint()
            constraint.type = Constraint.TYPE.PRIMARY_KEY.name
            return constraint
        }

        fun foreignkey(alfa:Constraint.()->Unit):Constraint {
            val constraint = Constraint()
            constraint.type = Constraint.TYPE.FOREIGN_KEY.name
            return constraint
        }

        fun addFunctional(alfa:Constraint.()->Unit):Constraint {
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


        fun set(exprs : String ):Set<Constraint> {
            if (exprs.equals("")) return HashSet<Constraint>()
            val replace = exprs.replace("\\s+", "")
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

        fun of(left:String, right:String):Constraint {
            var left = left.replace("\\s+","")
            var right = right.replace("\\s+","")
            val lefts = left.split(",")
            val rights = right.split(",")
            val c = Constraint()
            c.source.columns = cols(lefts)
            c.target.columns = cols(rights)
            return c
        }

        fun of(left:Collection<Column>, right:Collection<Column>):Constraint {
            val c = Constraint()
            c.source.columns.addAll(left)
            c.target.columns.addAll(right)
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

    override fun resetLabels(labelsAsString: String): String {
        labels = labelsAsString
        return labels!!
    }

    override fun addLabels(labelsAsString: String): String {
        if (labels==null) labels = labelsAsString
        else labels += labelsAsString
        return labels!!
    }

    override fun addLabels(newLabels: List<Label>): String {
        return addLabels(newLabels.joinToString(","))
    }

    override fun hasLabel(label: String): Boolean {
        return labels?.contains(label)?:false
    }

    override fun labelsAsString(): String {
        return labels?: ""
    }

    override fun toString(): String {
        if (source==null) return "no source"
        if (source.columns==null || source.columns.isEmpty()) return "no source columns"
        if (target==null) return "no target"
        if (target.columns==null || right().isEmpty()) return "no target columns"
        var result = type +" " +source.table+":"+source.columns[0].toString()
        for(col in source.columns.drop(1))
            result += "," + col.toString()
        result +=" --> " +target.table+":"
        if (target.columns.size>0) {
            result+= target.columns[0].toString()
            for (col in target.columns.drop(1))
                result += "," + col.toString()
        }
        result +=" ; "
        return result
    }

    override fun equals(other: Any?): Boolean {
        return this.toString().equals((other as Constraint).toString())
    }

    override fun hashCode(): Int {
        return this.toString().hashCode()
    }


}
