package unibz.cs.semint.kprime.domain.ddl

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

    fun table(name: String): Table? {
        if (tables.isEmpty()) return null
        return tables.filter { t -> t.name==name }.firstOrNull()
    }

    fun constraint(name: String): Constraint? {
        if (constraints.isEmpty()) return null
        return constraints.filter { c -> c.name==name}.firstOrNull()
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

    fun functionals(): Set<Constraint> {
        var resultCols = mutableSetOf<Column>()
        return constraints.filter { c ->
            c.type == Constraint.TYPE.FUNCTIONAL.name }.toSet()
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
        primaryConstraint.type= Constraint.TYPE.PRIMARY_KEY.name
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

    companion object {

        fun <T> reducedPowerSet(originalSet: Set<T>): Set<Set<T>> {
            var result = powerSet(originalSet)
            result = result.minus(HashSet<T>()) as Set<Set<T>>
            return result
        }

        private fun <T> powerSet(originalSet: Set<T>): Set<Set<T>> {
            val sets = HashSet<Set<T>>()
            if (originalSet.isEmpty()) {
                sets.add(HashSet<T>())
                return  sets
            }
            val list = ArrayList<T>(originalSet)
            val head = list.get(0)
            val rest = HashSet<T>(list.subList(1,list.size))
            for (set in powerSet(rest)) {
                val newSet = HashSet<T>()
                newSet.add(head)
                newSet.addAll(set)
                sets.add(newSet)
                sets.add(set)
            }
            return sets
        }


        fun superkeys(attrs: Set<Column>, fds:Set<Constraint>): Set<Set<Column>> {
            val keys = HashSet<Set<Column>>()
            val  powerset = reducedPowerSet(attrs)
            for (sa in powerset) {
                if (closure(sa, fds).equals(attrs)) {
                    keys.add(sa)
                }
            }
            return keys
        }

        fun closure(attrs: Set<Column>, fds:Set<Constraint>): Set<Column> {
            val result = HashSet<Column>(attrs)
            //println("RESULT X = $result")
            var found = true
            while(found) {
                found= false
                for (fd in fds) {
                    //println("FD ${fd.left()} == ${fd.right()}")
                    if (result.containsAll(fd.left())
                            && !result.containsAll(fd.right())) {
                        result.addAll(fd.right())
                        found = true
                        //println("FOUND")
                    }
                }
            }
            return result
        }

        fun keys(attrs: Set<Column>, fds:Set<Constraint>): Set<Set<Column>> {
            var superkeys = superkeys(attrs, fds)
            var toremove = HashSet<Set<Column>>()
            for (key in superkeys) {
                for (col in key) {
                    var remaining = HashSet<Column>(key)
                    remaining.remove(col)
                    if (superkeys.contains(remaining)) {
                        toremove.add(key)
                        break
                    }
                }

            }
            superkeys = superkeys.minus(toremove)
            return superkeys
        }

        fun removeTrivial(fds : Set<Constraint>): HashSet<Constraint> {
            val toRemove = HashSet<Constraint>()
            val toAdd = HashSet<Constraint>()
            for (fd in fds) {
                if (fd.left().containsAll(fd.right())) {
                    toRemove.add(fd)
                }
                else {
                    val toRemoveFromRight = HashSet<Column>()
                    for (a in fd.right()) {
                        if (fd.left().contains(a)) {
                            toRemoveFromRight.add(a)
                        }
                    }
                    if (toRemoveFromRight.isNotEmpty()) {
                        var right = fd.right()
                        right = right.minus(toRemoveFromRight) as Collection<Column>
                        toRemove.add(fd)
                        toAdd.add(Constraint.of(fd.left(),right))
                    }
                }
            }
            val result = HashSet<Constraint>(fds)
            result.addAll(toAdd)
            result.removeAll(toRemove)
            return result
        }
    }

}
