package unibz.cs.semint.kprime.domain.db.schemalgo

import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.Constraint

fun minimalBasis(fds: Set<Constraint>): Set<Constraint> {
    var result = HashSet<Constraint>(fds)
    result = splitRight(result)
    result = removeTrivial(result)
    var count = 1
    while (count > 0) {
        val before_remove = result.size
        result = removeUnnecessaryEntireFD(result)
        result = removeUnnecessaryLeftSide(result)
        count = before_remove - result.size
    }
    return result
}

private fun removeUnnecessaryLeftSide(fds: HashSet<Constraint>): HashSet<Constraint> {
    var count = 0
    while (true) {
        var found = false
        lateinit var  toRemove: Constraint
        lateinit var toAdd: Constraint
        var loop = 0
        for (fd in fds) {
            val left = fd.left()
            val right = fd.right()
            if (left.size > 1) {
                for (a in left) {
                    val remaining: MutableSet<Column> = HashSet<Column>(left)
                    remaining.remove(a)
                    val alternative: MutableSet<Constraint> = HashSet<Constraint>(fds)
                    alternative.remove(fd)
                    toAdd = Constraint.of(remaining,right)
                    alternative.add(toAdd)
                    if (equivalent(alternative, fds)) {
                        found = true
                        toRemove = fd
                        ++count
                        break
                    }
                }
            }
            if (found) {
                break
            }
            ++loop
        }
        if (found) {
            fds.remove(toRemove)
            fds.add(toAdd)
        }
        if (loop == fds.size) {
            break
        }
    }
    return fds

}

