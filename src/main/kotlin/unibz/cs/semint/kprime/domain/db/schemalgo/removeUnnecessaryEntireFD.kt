package unibz.cs.semint.kprime.domain.db.schemalgo

import unibz.cs.semint.kprime.domain.db.Constraint

fun removeUnnecessaryEntireFD(fds: Set<Constraint>): HashSet<Constraint> {
    var temp = HashSet<Constraint>(fds)
    var count = 0
    while(true) {
        lateinit var toRemove : Constraint
        var found = false
        for (fd in temp) {
            val remaining = HashSet<Constraint>(temp)
            remaining.remove(fd)
            //println("REMOVE ")
            if (equivalent(remaining, temp)) {
                //println("EQUIVALENT $count")
                ++count
                found = true
                toRemove = fd
                break;
            }
        }
        if(!found) { break; }
        else {
            if (toRemove!=null)
                temp = temp.minus(toRemove) as HashSet<Constraint>
        }
    }
    return temp
}

