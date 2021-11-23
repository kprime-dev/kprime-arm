package unibz.cs.semint.kprime.domain.ddl.schemalgo

import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Constraint

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

