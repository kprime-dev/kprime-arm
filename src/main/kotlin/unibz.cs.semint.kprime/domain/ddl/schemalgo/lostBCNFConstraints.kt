package unibz.cs.semint.kprime.domain.ddl.schemalgo

import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Constraint

fun lostBCNFConstraints(attrs : Set<Column>, fds: Set<Constraint>): Set<Constraint> {
    val violations = checkBCNF(attrs, fds)
    if (violations.isEmpty()) return emptySet()

    val result = decomposeToBCNF(attrs,fds)

    var resultConstraints = HashSet<Set<Constraint>>()
    for (relation in result) {
//        println("columns")
//        println(relation.table.columns)
        //resultTables.add(relation.table.columns)
//        println("constr")
//        println(relation.constraints)
        resultConstraints.add(relation.constraints)
//        println()
    }

    var lost = mutableSetOf<Constraint>()
    for (constraint in fds) {
        if (!resultConstraints.contains((setOf(constraint)))) {
//            println ( "Lost "+constraint.toString())
            lost.add(constraint)

        }
    }
    return lost
}

