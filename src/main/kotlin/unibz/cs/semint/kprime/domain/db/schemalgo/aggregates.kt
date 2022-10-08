package unibz.cs.semint.kprime.domain.db.schemalgo

import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.db.Schema
import unibz.cs.semint.kprime.domain.db.Table

fun aggregates(schema:Schema):List<Table> {
    var result = mutableListOf<Table>()

    val functionals = schema.constraintsByType(Constraint.TYPE.FUNCTIONAL)
    for (functional in functionals) {
        val cols = mutableListOf<Column>()
        cols.addAll(functional.left())
        cols.addAll(functional.right())
        val table = Table() withCols cols.toSet()
        result.add(table)
    }
    return result
}

fun chain(col:Column):List<Column>{
    return Column.set("col1,col2").toList()
}