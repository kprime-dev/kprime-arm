package unibz.cs.semint.kprime.domain.ddl.schemalgo

import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Schema
import unibz.cs.semint.kprime.domain.ddl.Table
import unibz.cs.semint.kprime.domain.dml.ChangeSet

// FIXME in realtà non dovrebbe fermarsi al primo livello ma proseguire in modo ricorsivo e cominciare dalla coda.
// FIXME in realtà dovrebbe rimuovere tutti i constraint prima di operare le sostituzioni di colonna chiave per poi rimetterli.
// FIXME dovrebbe anche mantenere aggiorato lo schema logico.
fun oid(schema: Schema, originTableName: String): ChangeSet {
        val changeSet = ChangeSet()
        var sqlCommands = mutableListOf<String>()

        // given a origin-table with primary key (pk)
        val originTable = schema.table(originTableName)
        val originTableKey = schema.keyCols(originTableName)

        val originTableName2 = originTableName
        // adds one column autoincrement to origin table
        val sid = "sid$originTableName"
        sqlCommands.add("ALTER TABLE $originTableName ADD COLUMN $sid int NOT NULL auto_increment UNIQUE")
        schema.table(originTableName)!!.columns.add(Column.of(sid))
        // TODO NOTA BENE !!! non togliere lo spazio iniziale altrimenti originTableName assume il valore  di surrogateTableName.
        schema.addSurrogateKey(" "+originTableName+":"+sid)
        //TODO changeSet.alterTable!!.add(AlterTable() onTable originTableName withStatement "ADD COLUMN $sid int NOT NULL auto_increment UNIQUE")

        // create a key-table with projection of oid and pk
        val keyCols = originTableKey.map { c -> c.name }.joinToString(",")
        val surrogateTableName = "SKEY$originTableName"
        sqlCommands.add("CREATE TABLE $surrogateTableName AS SELECT $sid,$keyCols FROM $originTableName")
        schema.addTable("$surrogateTableName:$sid,$keyCols")
        schema.addSurrogateKey("$surrogateTableName:$sid")
        //TODO changeSet.createTable.add(CreateTable() name "SKEY$originTableName" withCols Column.set("$sid,$keyCols"))

        // search ftables with foreign keys od double-inc as pk
        updateReferencedTables(schema, originTableName, originTable, surrogateTableName, sid, originTableKey, sqlCommands)

        schema.copyConstraintsFromTableToTable(originTableName,surrogateTableName)
        // crea una double-inc tra tabella-chiave e tabella origine.
        //TODO val index = (schema.constraints?.size?: 0) + changeSet.size()
        //TODO changeSet plus SchemaCmdParser.parseDoubleInclusion(index,"SKEY$originTableName:$sid<->${originTableName}:$sid")
        schema.addDoubleInc("$surrogateTableName:$sid<->${originTableName}:$sid")
        schema.moveConstraintsFromColsToCol(originTableName,keyCols,sid)

        sqlCommands.add("ALTER TABLE $originTableName DROP COLUMN $keyCols")
        schema.table(originTableName)!!.columns.removeAll(originTableKey)
        //TODO changeset remove columns

        changeSet.sqlCommands = sqlCommands
        return changeSet
}

private fun updateReferencedTables(schema: Schema, originTableName: String, originTable: Table?, surrogateTableName: String, sid: String, originTableKey: Set<Column>, sqlCommands: MutableList<String>) {
        for (rTable in schema.referencedTablesOf(originTableName)) {
                val rTableColsToKeep = schema.table(rTable.name)!!.columns.toMutableSet()
                rTableColsToKeep.removeAll(originTable!!.columns)
                val notKeyCols = rTableColsToKeep.map { notKeyColName -> "${rTable.name}.$notKeyColName" }.joinToString(",")
                // crea una nuova tabella con chiave surrogata + attributi non chiave
                val rTableNewName = "${rTable.name}_1"
                // FIXME funziona solo per chiavi a colonna singola
                val newTableCommand = "CREATE TABLE ${rTableNewName} AS SELECT $surrogateTableName.$sid,$notKeyCols FROM SKEY$originTableName JOIN ${rTable.name} ON SKEY$originTableName.${originTableKey.first()} = ${rTable.name}.${originTableKey.first()}"
                // TODO changeSet.createTable.add(CreateTable() name rTableNewName withCols Column.set("SKEY$originTableName.$sid,$notKeyCols"))
                schema.addTable("${rTableNewName}:$surrogateTableName.$sid,$notKeyCols")
                schema.addSurrogateKey("$rTableNewName:$sid")

                schema.moveConstraintsFromTableToTable(rTable.name, rTableNewName)

                val rTableKeys = schema.keyCols(rTableNewName).map { col -> col.name }.joinToString(",")
//                println(schema.constraints)
//                println(schema.constraintsByTable(rTableNewName))
//                println(schema.keys(rTableNewName))
//                println("OID moveConstraintsFromColsToCol ${rTableNewName}:$rTableKeys to $rTableNewName:$sid")
                schema.moveConstraintsFromColsToCol(rTableNewName, rTableKeys, sid)
                schema.removeKeyConstraint(rTableNewName)


                sqlCommands.add(newTableCommand)
        }
}