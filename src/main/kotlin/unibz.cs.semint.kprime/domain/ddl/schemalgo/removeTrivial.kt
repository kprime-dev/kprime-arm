package unibz.cs.semint.kprime.domain.ddl.schemalgo

import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Constraint

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

