package unibz.cs.semint.kprime.domain.db.schemalgo

import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.db.Schema
import unibz.cs.semint.kprime.domain.db.Table

fun aggregates(schema:Schema):List<Table> {
    var result = mutableListOf<Table>()

    schema.constraintsByTable("tabName").filter { it.hasTypeFunctional() }
    val functionals = schema.constraintsByType(Constraint.TYPE.FUNCTIONAL)
    val subfunc = mutableListOf<Constraint>()
    for (functional in functionals) {
        if (subfunc.contains(functional)) continue
        val cols = mutableListOf<Column>()
        cols.addAll(functional.left())
        cols.addAll(functional.right())
        // if any right col is key (left in some other fd) the add right cols of these fd
        for (col in functional.right()) {
            for (subfunctional in functionals) {
                if (subfunctional.left().contains(col)) cols.addAll(subfunctional.right())
                subfunc.add(subfunctional)
            }
        }
        val table = Table() withCols cols.toSet()
        result.add(table)
    }
    return result
}

fun chain(col:Column):List<Column>{
    return Column.set("col1,col2").toList()
}