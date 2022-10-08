package unibz.cs.semint.kprime.domain.db.schemalgo

import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.Constraint

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

