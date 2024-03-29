package unibz.cs.semint.kprime.domain.db.schemalgo

import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.Constraint

fun closure(attrs: Set<Column>, fds:Set<Constraint>): Set<Column> {
    val result = HashSet<Column>(attrs)
    //println("RESULT X = $result")
    var found = true
    while(found) {
        found= false
        for (fd in fds) {
            //println("FD ${fd.left()} == ${fd.right()}")
            if (result.containsAll(fd.left())
                    && !result.containsAll(fd.right())) {
                result.addAll(fd.right())
                found = true
                //println("FOUND")
            }
        }
    }
    return result
}

