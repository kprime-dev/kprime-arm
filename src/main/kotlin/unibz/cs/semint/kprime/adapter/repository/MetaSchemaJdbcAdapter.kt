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

    override fun metaDatabase(datasource: DataSource,db : Database) : Database {
            val user = datasource.user
            val pass = datasource.pass
            val path = datasource.path
            val table = "" // TODO accept a single table name as parameter

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


            if(db.name.isNullOrEmpty()) db.name="sourceName"
            if(db.id.isNullOrEmpty()) db.id=UUID.randomUUID().toString()
            if(db.schema.name.isNullOrEmpty()) db.schema.name="sourceName"
            if(db.schema.id.isNullOrEmpty()) db.schema.id=UUID.randomUUID().toString()
            var tableNames  =  mutableListOf<String>()
            if (table.isNotEmpty()) {
                tableNames= mutableListOf(table)
            } else {
                tableNames.addAll(readTables(metaData,db))
            }
            tableNames.addAll(readViews(metaData,db))
            readColumns(metaData, tableNames,db)
            readPrimaryKeys(metaData, tableNames,db)
            readForeignKeys(metaData, tableNames,db)
            conn.close()
        return db
    }

    internal fun readTables(metaData: DatabaseMetaData, db: Database):List<String> {
        val tables = metaData.getTables(null, null, null, arrayOf("TABLE"))
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

    private fun readViews(metaData: DatabaseMetaData, db: Database):List<String> {
        val views = metaData.getTables(null, null, null, arrayOf("VIEW"))
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

    private fun readColumns(metaData: DatabaseMetaData, tableNames: List<String>, db: Database) {
        for (tableName in tableNames) {
            val columns = metaData.getColumns(null, null, tableName, null)
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

    private fun readPrimaryKeys(metaData: DatabaseMetaData, tableNames: List<String>, db: Database) {
        //println("-----------readPrimaryKeys")
        for (tableName in tableNames) {
            val primaryKeys = metaData.getPrimaryKeys(null, null, tableName)
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

    private fun readForeignKeys(metaData: DatabaseMetaData, tableNames: List<String>, db: Database) {
        for (tableName in tableNames) {
            val fkeys = metaData.getImportedKeys(null, null, tableName)
            while (fkeys.next()) {
                //println("   " + fkeys.getString("PKTABLE_NAME") + " --- " + fkeys.getString("PKCOLUMN_NAME") + " === " + fkeys.getString("FKCOLUMN_NAME"))
                val constr = Constraint()
                constr.type= Constraint.TYPE.FOREIGN_KEY.name
                constr.name=fkeys.getString("PKTABLE_NAME") + "." + fkeys.getString("PKCOLUMN_NAME")
                constr.source= Source()
                constr.source.name=tableName
                val colSource = Column()
                colSource.name=fkeys.getString("FKCOLUMN_NAME")
                constr.source.columns.add(colSource)
                constr.target= Target()
                constr.target.name=fkeys.getString("PKTABLE_NAME")
                val colTarget = Column()
                colTarget.name=fkeys.getString("PKCOLUMN_NAME")
                constr.target.columns.add(colTarget)
                db.schema.constraints().add(constr)
            }
        }
    }


}