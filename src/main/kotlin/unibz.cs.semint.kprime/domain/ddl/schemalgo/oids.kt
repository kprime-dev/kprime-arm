package unibz.cs.semint.kprime.domain.ddl.schemalgo

import unibz.cs.semint.kprime.domain.ddl.Schema

fun oid(schema: Schema, originTableName: String): List<String> {
        var sqlCommands = mutableListOf<String>()
        // given a origin-table with primary key (pk)
        val originTable = schema.table(originTableName)
        val originTableKey = schema.key(originTableName)
        // adds one column autoincrement
        sqlCommands.add("ALTER TABLE $originTableName ADD COLUMN sid int NOT NULL auto_increment UNIQUE")
        // create a key-table with projection of oid and pk
        val keyCols = originTableKey.map { c -> c.name }.joinToString(",")
        sqlCommands.add("CREATE TABLE SKEY$originTableName AS SELECT sid,$keyCols FROM $originTableName")
        // search ftables with foreign keys od double-inc as pk
        // var ftables = schema.foreignsTable(originTableName)
        // for each ftable adds one column oid with join with corresponding to fkey values
        // replace pk origin-table
        // replace fks to new surrogate id column
        // remove ex-pk columns from origin-table
        sqlCommands.add("ALTER TABLE $originTableName DROP COLUMN $keyCols")
        // remove ex-pk columns from fk tables
        return sqlCommands
}