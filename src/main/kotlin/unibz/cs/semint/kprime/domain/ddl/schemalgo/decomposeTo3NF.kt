package unibz.cs.semint.kprime.domain.ddl.schemalgo

import unibz.cs.semint.kprime.domain.ddl.*

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


