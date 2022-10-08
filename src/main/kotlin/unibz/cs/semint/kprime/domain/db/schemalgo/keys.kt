package unibz.cs.semint.kprime.domain.db.schemalgo

import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.Constraint

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

