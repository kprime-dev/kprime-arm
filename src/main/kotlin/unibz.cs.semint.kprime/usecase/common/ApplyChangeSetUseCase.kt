package unibz.cs.semint.kprime.usecase.common

import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dml.*
import unibz.cs.semint.kprime.usecase.service.IXMLSerializerService

/**
 * Given a Databse will apply changes following a given changeSet modification.
 */
class ApplyChangeSetUseCase(serializer : IXMLSerializerService) {

    val serializer = serializer

    fun apply(db: Database, changeset: ChangeSet): Database {
        var newdb = serializer.deepclone(db)
        for (dt in changeset.dropTable) { newdb = dropTable(newdb,dt) }
        for (dc in changeset.dropConstraint) { newdb = dropConstraint(newdb,dc) }
        for (dm in changeset.dropMapping) { newdb = dropMapping(newdb,dm) }
        for (cc in changeset.createConstraint) { newdb = createConstraint(newdb,cc) }
        for (ct in changeset.createTable) { newdb = createTable(newdb,ct) }
        for (cm in changeset.createMapping) {newdb = createMapping(newdb,cm) }
        return newdb
    }

    private fun createMapping(db: Database, cm: CreateMapping): Database {
        if (db.mappings==null) return db
        db.mappings!!.add(cm)
        return db
    }

    fun createTable(db:Database, createTable: CreateTable): Database {
        db.schema.tables().add(createTable)
        return db
    }
    fun createConstraint(db:Database, createConstraint: CreateConstraint): Database {
        db.schema.constraints().add(createConstraint)
        return db
    }
    fun dropTable(db:Database, dropTable: DropTable):Database {
        if (db.schema.tables().isEmpty()) return db
        if (db.schema==null) return db
        val t= db.schema.table(dropTable.tableName)
        if (t!=null){ db.schema.tables().remove(t)}
        return db
    }

    fun dropConstraint(db:Database, dropConstraint: DropConstraint):Database {
        if (db.schema.constraints().isEmpty()) return db
        val c = db.schema.constraint(dropConstraint.constraintName)
                .let { it ->  if(it !=null) db.schema.constraints().remove(it)}
        return db
    }

    fun dropMapping(db:Database, dropMapping: DropMapping):Database {
        if (db.mappings == null || db.mappings!!.isEmpty()) return db
        val c = db.mapping(dropMapping.name)
                .let { it ->  if(it !=null) db.mappings!!.remove(it)}
        return db
    }

}