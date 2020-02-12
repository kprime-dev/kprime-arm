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

        private fun <T> reducedPowerSet(originalSet: Set<T>): Set<Set<T>> {
            var result = powerSet(originalSet)
            result = result.minus(HashSet<T>()) as Set<Set<T>>
            return result
        }

        fun <T> powerSet(originalSet: Set<T>): Set<Set<T>> {
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

        fun equivalent(a: Set<Constraint>, b: Set<Constraint>): Boolean {
            val names = HashSet<Column>()
            for (fd in a) {
                names.addAll(fd.left())
                names.addAll(fd.right())
            }
            for (fd in b) {
                names.addAll(fd.left())
                names.addAll(fd.right())
            }
            val reducedPowerSet = reducedPowerSet(names)
            for (set in reducedPowerSet) {
                val closureInA = closure(set,a)
                val closureInB = closure(set,b)
                if (!closureInA.equals(closureInB)) return false
            }
            return true
        }

        fun removeUnnecessaryEntireFD(fds: Set<Constraint>): HashSet<Constraint> {
            var temp = HashSet<Constraint>(fds)
            var count = 0
            while(true) {
                lateinit var toRemove : Constraint
                var found = false
                for (fd in temp) {
                    val remaining = HashSet<Constraint>(temp)
                    remaining.remove(fd)
                    //println("REMOVE ")
                    if (equivalent(remaining, temp)) {
                        println("EQUIVALENT $count")
                        ++count
                        found = true
                        toRemove = fd
                        break;
                    }
                }
                if(!found) { break; }
                else {
                    if (toRemove!=null)
                        temp = temp.minus(toRemove) as HashSet<Constraint>
                }
            }
            return temp
        }

        fun splitRight(fds: Set<Constraint>): HashSet<Constraint> {
            val result = HashSet<Constraint>(fds)
            val toRemove = HashSet<Constraint>()
            val toAdd = HashSet<Constraint>()
            for(fd in fds) {
                if (fd.right().size > 1) {
                    for (a in fd.right()) {
                        toAdd.add(Constraint.of(fd.left(), listOf(a)))
                    }
                    toRemove.add(fd)
                }
            }
            result.addAll(toAdd)
            result.removeAll(toRemove)
            return result
        }

        fun projection(attrs: Set<Column>, fds: Set<Constraint>): Set<Constraint> {
            val appeared = HashSet<Column>()
            for (fd in fds) {
                appeared.addAll(fd.left())
                appeared.addAll(fd.right())
            }
            if (attrs.containsAll(appeared)) {
                return HashSet<Constraint>(fds)
            }
            val notin = HashSet<Column>(appeared)
            notin.removeAll(attrs)
            val reducedPowerSet = reducedPowerSet(attrs)
            val result = HashSet<Constraint>()
            for (sa in reducedPowerSet) {
                var closure = closure(sa, fds)
                closure = closure.minus(notin)
                result.add(Constraint.of(sa, closure))
            }
            return minimalBasis(result)
        }

        fun minimalBasis(fds: Set<Constraint>): Set<Constraint> {
            var result = HashSet<Constraint>(fds)
            result = splitRight(result)
            result = removeTrivial(result)
            var count = 1
            while (count > 0) {
                val before_remove = result.size
                result = removeUnnecessaryEntireFD(result)
                result = removeUnnecessaryLeftSide(result)
                count = before_remove - result.size
            }
            return result
        }

        private fun removeUnnecessaryLeftSide(fds: HashSet<Constraint>): HashSet<Constraint> {
            var count = 0
            while (true) {
                var found = false
                lateinit var  toRemove: Constraint
                lateinit var toAdd: Constraint
                var loop = 0
                for (fd in fds) {
                    val left = fd.left()
                    val right = fd.right()
                    if (left.size > 1) {
                        for (a in left) {
                            val remaining: MutableSet<Column> = HashSet<Column>(left)
                            remaining.remove(a)
                            val alternative: MutableSet<Constraint> = HashSet<Constraint>(fds)
                            alternative.remove(fd)
                            toAdd = Constraint.of(remaining,right)
                            alternative.add(toAdd)
                            if (equivalent(alternative, fds)) {
                                found = true
                                toRemove = fd
                                ++count
                                break
                            }
                        }
                    }
                    if (found) {
                        break
                    }
                    ++loop
                }
                if (found) {
                    fds.remove(toRemove)
                    fds.add(toAdd)
                }
                if (loop == fds.size) {
                    break
                }
            }
            return fds

        }

    }

}
