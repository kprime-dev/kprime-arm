package unibz.cs.semint.kprime.domain.ddl

object SchemaAlgo {

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
                    //println("EQUIVALENT $count")
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

    fun combineRight(fds: Set<Constraint>): Set<Constraint> {
        val result = HashSet<Constraint>(fds)
        val map = HashMap<Collection<Column>, MutableCollection<Column>>()
        for (fd in result) {
            if(map.containsKey(fd.left())) {
                map.get(fd.left())?.addAll(fd.right())
            } else {
                map.put(fd.left(), fd.right() as MutableCollection<Column>)
            }
        }
        result.clear()
        for (left in map.keys) {
            result.add(Constraint.of(left,map.get(left) as MutableCollection))
        }
        return result
    }

    fun check3NF(attrs : Set<Column>, fds : Set<Constraint>) :Set<Constraint> {
        val keysSet = keys(attrs,fds)
        val primes = HashSet<Column>()
        for (keys in keysSet)
            primes.addAll(keys)
        val violations = HashSet<Constraint>()
        for (fd in fds) {
            if(!primes.containsAll(fd.right())) {
                var contains = false
                for (keys in keysSet) {
                    if (fd.left().containsAll(keys)) {
                        contains = true
                        break
                    }
                }
                if (!contains) {
                    violations.add(fd)
                }
            }
        }
        return violations
    }

    fun checkBCNF(attrs: Set<Column>, fds:Set<Constraint>) : Set<Constraint> {
        val keysSet = keys(attrs,fds)
        val violations = HashSet<Constraint>()
        for (fd in fds) {
            var contains = false
            for (keys in keysSet) {
                if (fd.left().containsAll(keys)) {
                    contains = true
                    break
                }
                if (!contains) violations.add(fd)
            }
        }
        return violations
    }

    fun checkLossyDecomposition(attrs: Set<Column>, fds:Set<Constraint>, subattrs : Set<Set<Column>>)
            : Set<Constraint> {
        val lost = HashSet<Constraint>()
        val decomposed = HashSet<Constraint>()
        for (subattr in subattrs) {
            decomposed.addAll(projection(subattr,fds))
        }
        for (fd in fds) {
            val left = fd.left().toHashSet()
            val closure = closure(left,decomposed)
            if (!closure.containsAll(fd.right())) {
                lost.add(fd)
            }
        }
        return lost
    }

    fun decomposeTo3NF(attrs:Set<Column>, fds:Set<Constraint>): Set<Relation> {
        val result = HashSet<Relation>()
        val minimalBasis = minimalBasis(fds)
        decomposeOnPotentialRelations(minimalBasis, result)
        removeSubsumed(result)
        addMinimalBasisRelationIfExist(attrs, minimalBasis, result)
        return result
    }

    private fun addMinimalBasisRelationIfExist(attrs: Set<Column>, minimalBasis: Set<Constraint>, result: HashSet<Relation>) {
        val keys = keys(attrs, minimalBasis)
        var contains = false
        for (relationR in result) {
            for (key in keys) {
                if (relationR.table.columns.containsAll(key)) {
                    contains = true
                    break
                }
            }
            if (contains) break
        }
        if (!contains) {
            val key = keys.first()
            val proj = projection(key, minimalBasis)
            result.add(Relation(Table() withCols key, proj))
        }
    }

    private fun removeSubsumed(result: HashSet<Relation>) {
        val toRemove = HashSet<Relation>()
        for (relationA in result) {
            for (relationB in result) {
                if (relationA != relationB && relationA.table.columns.containsAll(
                                relationB.table.columns))
                    toRemove.add(relationB)
            }
        }
        result.removeAll(toRemove)
    }

    private fun decomposeOnPotentialRelations(minimalBasis: Set<Constraint>, result: HashSet<Relation>) {
        for (fd in minimalBasis) {
            val attrsNow = fd.left().toHashSet()
            attrsNow.addAll(fd.right())
            val projection = projection(attrsNow, minimalBasis)
            result.add(Relation(Table() withCols attrsNow, projection))
        }
    }


    fun decomposeToBCNF(attrs:Set<Column>, fds:Set<Constraint>): Set<Relation> {
        val result = HashSet<Relation>()

        val violations = checkBCNF(attrs, fds)
        if (violations.isEmpty()) {
            result.add(Relation(Table() withCols attrs, fds))
            return result
        }
        lateinit var pick : Constraint
        for (fd in violations) {
            pick = fd
            break
        }
        val lefts = pick.left()
        val attrs1 = closure(lefts.toSet(), fds)
        val attrs2 = HashSet<Column>(attrs)
        attrs2.removeAll(attrs1)
        attrs2.addAll(lefts)
        val fds1 = projection(attrs1, fds)
        val fds2 = projection(attrs2,fds)
        val relation1 = Relation(Table() withCols attrs1, fds1)
        val relation2 = Relation(Table() withCols attrs2, fds2)
        result.addAll(decomposeToBCNF(attrs1,fds1))
        result.addAll(decomposeToBCNF(attrs2,fds2))
        return result
    }

    fun lostBCNFConstraints(attrs : Set<Column>, fds: Set<Constraint>): Set<Constraint> {
        val violations = checkBCNF(attrs, fds)
        if (violations.isEmpty()) return emptySet()

        val result = decomposeToBCNF(attrs,fds)

        var resultConstraints = HashSet<Set<Constraint>>()
        for (relation in result) {
            println("columns")
            println(relation.table.columns)
            //resultTables.add(relation.table.columns)
            println("constr")
            println(relation.constraints)
            resultConstraints.add(relation.constraints)
            println()
        }

        var lost = mutableSetOf<Constraint>()
        for (constraint in fds) {
            if (!resultConstraints.contains((setOf(constraint)))) {
                println ( "Lost "+constraint.toString())
                lost.add(constraint)

            }
        }
        return lost
    }

}