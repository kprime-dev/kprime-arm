package unibz.cs.semint.kprime.domain

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

@JacksonXmlRootElement(localName = "schema")
class Schema () {
    @JacksonXmlProperty(isAttribute = true)
    var name: String =""
    @JacksonXmlProperty(isAttribute = true)
    var id: String=""

    var tables= ArrayList<Table>()
    var constraints= ArrayList<Constraint>()

    fun table(name: String):Table {
        return tables.filter { t -> t.name==name }.first()
    }

    fun key(tableName: String): Set<Column> {
        var resultCols = mutableSetOf<Column>()
        val first = constraints.filter { c ->
            c.type == Constraint.TYPE.PRIMARY_KEY.name &&
                    c.name == "primaryKey.${tableName}"
        }.toList()
        if (first.isEmpty()) return mutableSetOf()
        return first[0].source.columns.toSet()
    }

    fun functionalLHS(tableName: String): Set<Column> {
        var resultCols = mutableSetOf<Column>()
        val first = constraints.filter { c ->
            c.type == Constraint.TYPE.FUNCTIONAL.name &&
                    c.name == "functional.${tableName}"
        }.toList()
        if (first.isEmpty()) return mutableSetOf()
        return first[0].source.columns.toSet()
    }

    fun functionalRHS(tableName: String): Set<Column> {
        var resultCols = mutableSetOf<Column>()
        val first = constraints.filter { c ->
            c.type == Constraint.TYPE.FUNCTIONAL.name &&
                    c.name == "functional.${tableName}"
        }.toList()
        if (first.isEmpty()) return mutableSetOf()
        return first[0].target.columns.toSet()
    }

    fun key(tableName:String,k:Set<Column>) {
        val primaryConstraint = Constraint()
        primaryConstraint.name="primaryKey.$tableName"
        primaryConstraint.source.table="$tableName"
        primaryConstraint.source.columns.addAll(k)
        primaryConstraint.type=Constraint.TYPE.PRIMARY_KEY.name
        constraints.add(primaryConstraint)
    }

    fun functional(tableName:String, lhs:Set<Column>, rhs:Set<Column>){
        val functionalConstraint = Constraint()
        functionalConstraint.name="functional.$tableName"
        functionalConstraint.source.table="$tableName"
        functionalConstraint.source.columns.addAll(lhs)
        functionalConstraint.target.table="$tableName"
        functionalConstraint.target.columns.addAll(rhs)
        functionalConstraint.type= Constraint.TYPE.FUNCTIONAL.name
        constraints.add(functionalConstraint)

    }

}
