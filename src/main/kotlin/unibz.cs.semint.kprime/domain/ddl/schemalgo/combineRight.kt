package unibz.cs.semint.kprime.domain.ddl.schemalgo

import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Constraint

fun combineRight(fds: Set<Constraint>): Set<Constraint> {
    val result = HashSet<Constraint>(fds)
    val map = HashMap<Collection<Column>, MutableCollection<Column>>()
    for (fd in result) {
        if(map.containsKey(fd.left())) {
            map.get(fd.left())?.addAll(fd.right())
        } else {
            map.put(fd.left(), fd.right() as MutableCollection<Column>)
        }
    }
    result.clear()
    for (left in map.keys) {
        result.add(Constraint.of(left,map.get(left) as MutableCollection))
    }
    return result
}

