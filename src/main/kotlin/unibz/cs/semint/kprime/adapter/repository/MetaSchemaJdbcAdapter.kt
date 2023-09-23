package unibz.cs.semint.kprime.adapter.repository

import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.db.*
import unibz.cs.semint.kprime.domain.db.Target
import unibz.cs.semint.kprime.usecase.repository.IMetaSchemaRepository
import java.sql.DatabaseMetaData
import java.sql.DriverManager
import java.sql.JDBCType
import java.util.*


class MetaSchemaJdbcAdapter : IMetaSchemaRepository {

    override fun metaDatabase(datasource: DataSource,db : Database, tableName: String, catalog: String?, schema:String?) : Database {
            val user = datasource.user
            val pass = datasource.pass
            val path = datasource.path

        val connectionProps = Properties()
            connectionProps["user"] = user
            connectionProps["password"] = pass
            println("Looking for driver [${datasource.driver}] for connection [$path] with user [$user].")
            Class.forName(datasource.driver).newInstance()
//            println("--------------------------")
//            DriverManager.getDrivers().toList().forEach{ println(it.toString()) }
//            println("--------------------------")

            val conn = DriverManager.getConnection(
                     path, connectionProps)
            val metaData = conn.metaData


            if(db.name.isEmpty()) db.name="sourceName"
            if(db.id.isEmpty()) db.id=UUID.randomUUID().toString()
            if(db.schema.name.isEmpty()) db.schema.name="sourceName"
            if(db.schema.id.isEmpty()) db.schema.id=UUID.randomUUID().toString()
            var tableNames  =  mutableListOf<String>()
        if (tableName.isNotEmpty()) {
            tableNames = mutableListOf(tableName)
            readTable(metaData, db, tableName, catalog, schema)
        } else {
            tableNames.addAll(readTables(metaData, db, catalog, schema))
            tableNames.addAll(readViews(metaData, db, catalog, schema))
        }
        readColumns(metaData, tableNames, db, catalog, schema)
        readPrimaryKeys(metaData, tableNames, db, catalog, schema)
        readForeignKeys(metaData, tableNames, db, catalog, schema)
        conn.close()
        return db
    }

    internal fun readTables(metaData: DatabaseMetaData, db: Database, catalog: String?, schema: String?):List<String> {
        val tables = metaData.getTables(catalog, schema, null, arrayOf("TABLE"))
        val tableNames = mutableListOf<String>()
        while (tables.next()) {
            val tableName = tables.getString("TABLE_NAME")
            tableNames.add(tableName)
            val table = Table()
            table.name=tableName
            db.schema.tables().add(table)
        }
        return tableNames
    }

    private fun readTable(metaData: DatabaseMetaData, db: Database, tableName:String, catalog: String?, schema: String?) {
        val tables = metaData.getTables(catalog, schema, null, arrayOf("TABLE"))
        while (tables.next()) {
            val currentTableName = tables.getString("TABLE_NAME")
            if (currentTableName == tableName) {
                val table = Table()
                table.name = currentTableName
                db.schema.tables().add(table)
            }
        }
    }

    private fun readViews(metaData: DatabaseMetaData, db: Database, catalog: String?, schema: String?):List<String> {
        val views = metaData.getTables(catalog, schema, null, arrayOf("VIEW"))
        val viewNames = mutableListOf<String>()
        //println("++++++++++++++++++++++++++++++++++++++++++++++++")
        //QueryJdbcAdapter().printResultSet(views)
        //println("++++++++++++++++++++++++++++++++++++++++++++++++")
        while (views.next()) {
            val viewName = views.getString("TABLE_NAME")
            //println(viewName)
            viewNames.add(viewName)
            val table = Table()
            table.name=viewName
            db.schema.tables().add(table)
        }
        return viewNames
    }

    private fun readColumns(metaData: DatabaseMetaData, tableNames: List<String>, db: Database, catalog: String?, schema: String?) {
        for (tableName in tableNames) {
            val columns = metaData.getColumns(catalog, schema, tableName, null)
            val colNames = mutableListOf<String>()
            while (columns.next()) {
                val colName = columns.getString("COLUMN_NAME")
                val colNullable = columns.getString("IS_NULLABLE")=="YES"
                colNames.add(colName)
                val column = Column()
                column.name=colName
                column.dbname=colName
                column.nullable=colNullable
                column.dbtype= JDBCType.valueOf(columns.getString("DATA_TYPE").toInt()).name
                db.schema.table(tableName).let { t -> t?.columns?.add(column) }
            }
        }
    }

    private fun readPrimaryKeys(metaData: DatabaseMetaData, tableNames: List<String>, db: Database, catalog: String?, schema: String?) {
        //println("-----------readPrimaryKeys")
        for (tableName in tableNames) {
            val primaryKeys = metaData.getPrimaryKeys(catalog, schema, tableName)
            //println("PRIMARY:")
            while (primaryKeys.next()) {
                //println("   " + primaryKeys.getString("COLUMN_NAME") + " === " + primaryKeys.getString("PK_NAME"))
                val constr = Constraint()
                constr.type= Constraint.TYPE.PRIMARY_KEY.name
                constr.name=primaryKeys.getString("PK_NAME")
                constr.source= Source()
                constr.source.name=tableName
                constr.source.table=tableName
                val colSource = Column()
                colSource.name=primaryKeys.getString("COLUMN_NAME")
                constr.source.columns.add(colSource)
                constr.target.columns.add(colSource)
                //constr.target= Target()
                db.schema.constraints().add(constr)
            }
        }
    }

    private fun readForeignKeys(metaData: DatabaseMetaData, tableNames: List<String>, db: Database, catalog: String?, schema: String?) {
        for (tableName in tableNames) {
            val fkeys = metaData.getImportedKeys(catalog, schema, tableName)
            while (fkeys.next()) {
                //println("   " + fkeys.getString("PKTABLE_NAME") + " --- " + fkeys.getString("PKCOLUMN_NAME") + " === " + fkeys.getString("FKCOLUMN_NAME"))
                val constr = Constraint()
                constr.type= Constraint.TYPE.FOREIGN_KEY.name
                constr.name=fkeys.getString("PKTABLE_NAME") + "." + fkeys.getString("PKCOLUMN_NAME")
                constr.source= Source()
                constr.source.name=tableName
                constr.source.table=tableName
                val colSource = Column()
                colSource.name=fkeys.getString("FKCOLUMN_NAME")
                constr.source.columns.add(colSource)
                constr.target= Target()
                constr.target.name=fkeys.getString("PKTABLE_NAME")
                constr.target.table=fkeys.getString("PKTABLE_NAME")
                val colTarget = Column()
                colTarget.name=fkeys.getString("PKCOLUMN_NAME")
                constr.target.columns.add(colTarget)
                db.schema.constraints().add(constr)
            }
        }
    }


}