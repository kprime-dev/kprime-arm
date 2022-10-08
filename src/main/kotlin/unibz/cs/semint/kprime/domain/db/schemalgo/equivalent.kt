package unibz.cs.semint.kprime.domain.db.schemalgo

import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.Constraint

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

