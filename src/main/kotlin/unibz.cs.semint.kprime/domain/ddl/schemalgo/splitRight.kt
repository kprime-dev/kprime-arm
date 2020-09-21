package unibz.cs.semint.kprime.domain.ddl.schemalgo

import unibz.cs.semint.kprime.domain.ddl.Constraint

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

