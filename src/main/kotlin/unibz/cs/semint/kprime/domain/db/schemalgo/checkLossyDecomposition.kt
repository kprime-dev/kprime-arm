package unibz.cs.semint.kprime.domain.db.schemalgo

import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.Constraint

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

