package unibz.cs.semint.kprime.domain.ddl.schemalgo

import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Constraint

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

