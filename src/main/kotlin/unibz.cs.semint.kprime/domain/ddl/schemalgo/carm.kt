package unibz.cs.semint.kprime.domain.ddl.schemalgo

fun carm() {
    // given one origin-table
    // if has oid
    // if table.arity > 2
    // while has arity > 2
        // given column pos > 2
        // create a new table projection of columns (oid,column) inheriting incident constraints on column
        // add constraint double-inc origin-table and new-table
        // remove column from origin-table with incident constraints
}