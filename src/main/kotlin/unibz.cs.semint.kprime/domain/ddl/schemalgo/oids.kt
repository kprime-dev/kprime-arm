package unibz.cs.semint.kprime.domain.ddl.schemalgo

import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Schema
import unibz.cs.semint.kprime.domain.dml.AlterTable
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.domain.dml.CreateTable

fun oid(schema: Schema, originTableName: String): ChangeSet {
        val changeSet = ChangeSet()
        var sqlCommands = mutableListOf<String>()
        // given a origin-table with primary key (pk)
        val originTable = schema.table(originTableName)
        val originTableKey = schema.key(originTableName)
        val sid = "sid$originTableName"
        // adds one column autoincrement
        sqlCommands.add("ALTER TABLE $originTableName ADD COLUMN $sid int NOT NULL auto_increment UNIQUE")
        schema.table(originTableName)!!.columns.add(Column.of(sid))
        //changeSet.alterTable!!.add(AlterTable() onTable originTableName withStatement "ADD COLUMN $sid int NOT NULL auto_increment UNIQUE")
        // create a key-table with projection of oid and pk
        val keyCols = originTableKey.map { c -> c.name }.joinToString(",")
        sqlCommands.add("CREATE TABLE SKEY$originTableName AS SELECT $sid,$keyCols FROM $originTableName")
        schema.addTable("SKEY$originTableName:$sid,$keyCols")
        //changeSet.createTable.add(CreateTable() name "SKEY$originTableName" withCols Column.set("$sid,$keyCols"))
        // search ftables with foreign keys od double-inc as pk
        // FIXME in realtà non dovrebbe fermarsi al primo livello ma proseguire in modo ricorsivo e cominciare dalla coda.
        // FIXME in realtà dovrebbe rimuovere tutti i constraint prima di operare le sostituzioni di colonna chiave per poi rimetterli.
        // FIXME dovrebbe anche mantenere aggiorato lo schema logico.
        val rTables = schema.referencedTablesOf(originTableName)
        println("==============REFERENCED:")
        for (rTable in rTables) {
                // remove constraint from old table (and remember)
                println(rTable.name)
                val rTableColsToKeep = schema.table(rTable.name)!!.columns.toMutableSet()
                rTableColsToKeep.removeAll(originTable!!.columns)
                val notKeyCols = rTableColsToKeep.map { notKeyColName -> "${rTable.name}.$notKeyColName" }.joinToString(",")
                val rTableNewName =  "${rTable.name}_1"
                val newTableCommand = "CREATE TABLE ${rTableNewName} AS SELECT SKEY$originTableName.$sid,$notKeyCols FROM SKEY$originTableName JOIN ${rTable.name} ON SKEY$originTableName.${originTableKey.first()} = ${rTable.name}.${originTableKey.first()}"
                schema.addTable("${rTableNewName}:SKEY$originTableName.$sid,$notKeyCols")
                changeSet.createTable.add(CreateTable() name rTableNewName withCols Column.set("SKEY$originTableName.$sid,$notKeyCols"))
                schema.constraintsFromTableToTable(rTable.name,rTableNewName)
                println(newTableCommand)
                sqlCommands.add(newTableCommand)
                // add constraint to new table (from memory)
        }
        println("________________________")


        // var ftables = schema.foreignsTable(originTableName)
        // for each ftable adds one column oid with join with corresponding to fkey values
        // replace pk origin-table
        // replace fks to new surrogate id column
        // remove ex-pk columns from origin-table
        sqlCommands.add("ALTER TABLE $originTableName DROP COLUMN $keyCols")
        schema.table(originTableName)!!.columns.removeAll(originTableKey)
        // remove ex-pk columns from fk tables
        changeSet.sqlCommands = sqlCommands
        return changeSet
}