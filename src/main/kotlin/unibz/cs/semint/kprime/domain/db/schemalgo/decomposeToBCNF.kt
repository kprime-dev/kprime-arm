package unibz.cs.semint.kprime.domain.db.schemalgo

import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.db.Relation
import unibz.cs.semint.kprime.domain.db.Table

fun decomposeToBCNF(attrs:Set<Column>, fds:Set<Constraint>): Set<Relation> {
    val result = HashSet<Relation>()

    val violations = checkBCNF(attrs, fds)
    if (violations.isEmpty()) {
        result.add(Relation(Table() withCols attrs, fds))
        return result
    }
    lateinit var pick : Constraint
    for (fd in violations) {
        pick = fd
        break
    }
    val lefts = pick.left()
    val attrs1 = closure(lefts.toSet(), fds)
    val attrs2 = HashSet<Column>(attrs)
    attrs2.removeAll(attrs1)
    attrs2.addAll(lefts)
    val fds1 = projection(attrs1, fds)
    val fds2 = projection(attrs2,fds)
    val relation1 = Relation(Table() withCols attrs1, fds1)
    val relation2 = Relation(Table() withCols attrs2, fds2)
    result.addAll(decomposeToBCNF(attrs1,fds1))
    result.addAll(decomposeToBCNF(attrs2,fds2))
    return result
}

