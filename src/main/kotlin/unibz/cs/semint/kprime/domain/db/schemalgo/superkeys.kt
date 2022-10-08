package unibz.cs.semint.kprime.domain.db.schemalgo

import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.Constraint

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

