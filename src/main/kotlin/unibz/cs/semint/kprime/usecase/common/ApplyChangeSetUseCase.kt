package unibz.cs.semint.kprime.usecase.common

import unibz.cs.semint.kprime.domain.ddl.Column
import unibz.cs.semint.kprime.domain.ddl.Constraint
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dml.*
import unibz.cs.semint.kprime.usecase.service.SerializerServiceI

/**
 * Given a Databse will apply changes following a given changeSet modification.
 */
class ApplyChangeSetUseCase(val serializer: SerializerServiceI) {

    fun apply(db: Database, changeset: ChangeSet): Database {
        var newdb = serializer.deepclone(db)
        for (dt in changeset.dropTable) { newdb = dropTable(newdb,dt) }
        for (dc in changeset.dropConstraint) { newdb = dropConstraint(newdb,dc) }
        for (dm in changeset.dropMapping) { newdb = dropMapping(newdb,dm) }
        for (cc in changeset.createConstraint) { newdb = createConstraint(newdb,cc) }
        for (ct in changeset.createTable) { newdb = createTable(newdb,ct,db) }
        for (cm in changeset.createMapping) {newdb = createMapping(newdb,cm) }
        if (changeset.alterTable!=null)
            for (at in changeset.alterTable!!) { newdb = alterTable(newdb,at) }
        return newdb
    }

    private fun alterTable(newdb: Database, at: AlterTable): Database {
        val table = newdb.schema.table(at.tableName) ?: return newdb
        // ALTER TABLE $tableName ADD COLUMN self INT
        if (at.statement.toLowerCase().contains("add column")) {
            val colName = at.statement.split(" ")[5]
            table.columns.add(Column.of(colName))
        }
        return newdb
    }

    private fun createMapping(newdb: Database, cm: CreateMapping): Database {
        if (newdb.mappings==null) return newdb
        newdb.mappings!!.add(cm)
        return newdb
    }

    fun createTable(newdb:Database, createTable: CreateTable, olddb:Database): Database {
        newdb.schema.tables().add(createTable)
        // TODO derive inherited constraints
//        println("ApplyChangeSetUseCase createTable ${createTable.view}")
//        if (createTable.view!=null && createTable.view.isNotEmpty()){
//            deriveConstraint(newdb, createTable, olddb)
//        }
        return newdb
    }

    // TODO derive inherited constraints
    private fun deriveConstraint(newdb : Database, createTable: CreateTable, olddb:Database) {
        println("=0")
        println(olddb.schema.tables().map { t -> t.name }.joinToString(","))
        val fromTable = olddb.schema.table(createTable.view) ?: return
        println("=1")
        println("check deriveConstraint from ${fromTable.name}")
        val contraintToCloneSource = mutableListOf<Constraint>()
        val contraintToCloneTarget = mutableListOf<Constraint>()
        for(col in createTable.columns) {
            println("check deriveConstraint col ${col.name}")
            val colDerived = fromTable.columns.filter { c -> c.name == col.name }.first()
            val sourceConstraints = olddb.schema.constraints().filter { costr -> costr.source.table.equals(fromTable.name) }
            for (constr in sourceConstraints) {
                if (!constr.left().filter { leftcol -> leftcol.name.equals(colDerived.name) }.isEmpty()) {
                    println("add LEFT constr ${constr.name}")
                    contraintToCloneSource.add(constr)
                }
            }
            val targetConstraints = olddb.schema.constraints().filter { costr -> costr.target.table.equals(fromTable.name) }
            for (constr in targetConstraints) {
                if (!constr.right().filter { rightcol -> rightcol.name.equals(colDerived.name) }.isEmpty()) {
                    println("add RIGHT constr ${constr.name}")
                    contraintToCloneTarget.add(constr)
                }
            }
        }
        for (oldConstr in contraintToCloneSource) {
            val newConstr = oldConstr.clone()
            newConstr.name = createTable.name+"."+newConstr.type.toLowerCase()+newdb.schema.constraints?.size
            newConstr.source.table = createTable.name
            if (oldConstr.hasTypeKey() || oldConstr.hasTypeFunctional()) {
                println("clone KEY or FUNC constr ${oldConstr.name}")
                cloneIfCreateTableHasAllCols(oldConstr, createTable, newConstr, newdb)
            } else {
                println("clone SOURCE constr ${oldConstr.name}")
                newdb.schema.constraints().add(newConstr)
            }
        }
        for (oldConstr in contraintToCloneTarget) {
            if (!oldConstr.hasTypeKey() && !oldConstr.hasTypeFunctional()) {
                println("clone TARGET constr ${oldConstr.name}")
                val newConstr = oldConstr.clone()
                newConstr.target.table = createTable.name
                newdb.schema.constraints().add(newConstr)
            }
        }
        println("END check deriveConstraint from ${fromTable.name}")
    }

    private fun cloneIfCreateTableHasAllCols(oldConstr: Constraint, createTable: CreateTable, newConstr: Constraint, newdb: Database) {
        val allCols = ArrayList<Column>()
        allCols.addAll(oldConstr.left())
        allCols.addAll(oldConstr.right())
        val allUniqueCols = allCols.toSet().map { c -> c.name }
        if (createTable.hasColumns(allUniqueCols)) {
            newConstr.target.table = createTable.name
            newdb.schema.constraints().add(newConstr)
        }
    }

    fun createConstraint(db:Database, createConstraint: CreateConstraint): Database {
//        println("apply create constraint ${createConstraint}")
        db.schema.constraints().add(createConstraint)
//        println("apply constraint size ${db.schema.constraints().size}")
        return db
    }

    fun dropTable(db:Database, dropTable: DropTable):Database {
        if (db.schema.tables().isEmpty()) return db
        if (db.schema==null) return db
        db.schema.dropTable(dropTable.tableName)
        return db
    }

    fun dropConstraint(db:Database, dropConstraint: DropConstraint):Database {
        if (db.schema.constraints().isEmpty()) return db
        if (dropConstraint.tableName.isNotEmpty()) {
            db.schema.constraintsByTable(dropConstraint.tableName).firstOrNull()
                    .let { it -> if (it != null) db.schema.constraints().remove(it) }
        } else {
            db.schema.constraint(dropConstraint.constraintName)
                    .let { it -> if (it != null) db.schema.constraints().remove(it) }
        }
        return db
    }

    fun dropMapping(db:Database, dropMapping: DropMapping):Database {
        if (db.mappings == null || db.mappings!!.isEmpty()) return db
        db.mapping(dropMapping.name)
                .let { it ->  if(it !=null) db.mappings!!.remove(it)}
        return db
    }

}