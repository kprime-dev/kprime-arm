package unibz.cs.semint.kprime.domain.ddl.schemalgo

import unibz.cs.semint.kprime.domain.ddl.Schema

fun oid(schema: Schema, originTableName: String): List<String> {
        var sqlCommands = mutableListOf<String>()
        // given a origin-table with primary key (pk)
        val originTable = schema.table(originTableName)
        val originTableKey = schema.key(originTableName)
        // adds one column autoincrement
        
        // create a key-table with projection of oid and pk
        // search ftables with foreign keys as pk
        // for each ftable adds one column oid with join with corresponding to fkey values
        // replace pk origin-table
        // replace fks to new surrogate id column
        // remove ex-pk columns from origin-table
        // remove ex-pk columns from fk tables
        return sqlCommands
}