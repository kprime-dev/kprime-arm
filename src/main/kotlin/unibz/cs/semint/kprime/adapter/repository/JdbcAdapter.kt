package unibz.cs.semint.kprime.adapter.repository

import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.dql.Query
import unibz.cs.semint.kprime.usecase.common.SQLizeSelectUseCase
import java.lang.IllegalArgumentException
import java.net.URL
import java.net.URLClassLoader
import java.sql.*
import java.util.*
import java.util.logging.Logger


class JdbcAdapter {


    private var formatted=false
    constructor() {}
    constructor(formatted: Boolean) {
        this.formatted = formatted
    }

    fun query(datasource: DataSource, query: Query, printer: (rs :ResultSet)->String) :String {
        val sqlquery = SQLizeSelectUseCase().sqlize(query)
        return query(datasource,sqlquery,printer)
    }

    fun query(datasource: DataSource, sqlquery: String, printer:(rs:ResultSet)->String):String {
        val conn: Connection = openConnection(datasource) ?: throw IllegalArgumentException("No connection")
        val sqlnative = conn.nativeSQL(sqlquery)
        val prepareStatement = conn.prepareStatement(sqlnative)
        val resultSet = prepareStatement.executeQuery()
        val result =printer(resultSet)
        resultSet.close()
        closeConnection(datasource, conn)
        return result
    }

    fun create(datasource: DataSource, sqlcreate: String) {
        val conn = openConnection(datasource) ?: throw IllegalArgumentException("No connection")
        val createStatement = conn.createStatement()
        val resultSet = createStatement.executeUpdate(sqlcreate)
//        println(" Create result : $resultSet")
        createStatement.close()
        closeConnection(datasource,conn)
    }

    private fun closeConnection(datasource: DataSource, conn: Connection) {
        val connectionDescriptor = datasource.connection
//        println(" Check close connection ${connectionDescriptor?.id}: ${connectionDescriptor?.closed}")
        if (connectionDescriptor != null) {
            if (connectionDescriptor.closed) {
                conn.close()
                datasource.remResource(connectionDescriptor.id)
//                println("closed connection ${connectionDescriptor.id}")
            }
        } else {
            conn.close()
        }
    }

    fun commit(datasource: DataSource) {
        val conn = openConnection(datasource) ?: throw IllegalArgumentException("No connection")
        conn.commit()
    }

    internal class DriverShim(d: Driver) : Driver {
        private val driver: Driver

        init {
            driver = d
        }

        @Throws(SQLException::class)
        override fun acceptsURL(u: String?): Boolean {
            return driver.acceptsURL(u)
        }

        @Throws(SQLException::class)
        override fun connect(u: String?, p: Properties?): Connection {
            return driver.connect(u, p)
        }

        @Throws(SQLException::class)
        override fun getPropertyInfo(u: String?, p: Properties?): Array<DriverPropertyInfo> {
            return driver.getPropertyInfo(u, p)
        }

        override fun getMajorVersion(): Int {
            return driver.majorVersion
        }

        override fun getMinorVersion(): Int {
            return driver.minorVersion
        }

        override fun jdbcCompliant(): Boolean {
            return driver.jdbcCompliant()
        }

        override fun getParentLogger(): Logger {
            return driver.parentLogger
        }
    }


    private fun openConnection(datasource: DataSource): Connection? {
        val source = datasource
        val user = source.user
        val pass = source.pass
        val path = source.path

        println("Looking for... driver [${source.driver}][${source.driverUrl}] for connection [$path] with user [$user].")
        if (source.driverUrl.isNotEmpty()) {
            val u = URL("jar:file:${source.driverUrl}!/")// /home/nipe/Temp/postgresql-42.2.8.jar
            val classname = source.driver
            val ucl = URLClassLoader(arrayOf(u) )
            val d = Class.forName(classname, true, ucl).newInstance() as Driver
            DriverManager.registerDriver(DriverShim(d))
        } else {
            Class.forName(source.driver).newInstance()
        }

        var conn: Connection?
//        println("Connection preparing...")
        if (datasource.connection == null) {
//            println("Connection NEW")
            val connectionProps = Properties()
            connectionProps.put("user", user)
            connectionProps.put("password", pass)
            conn = DriverManager.getConnection(
                    path, connectionProps)
            conn.autoCommit = true
        } else {
//            println("Connection from POOL ${datasource.connection?.id}")
            var resource = datasource.getResource(datasource.connection?.id!!)
            if (resource == null) {
                val connectionProps = Properties()
                connectionProps.put("user", datasource.connection?.username)
                connectionProps.put("password", datasource.connection?.pass)
                conn = DriverManager.getConnection(path, connectionProps)
                conn.autoCommit = datasource.connection?.commited!!
                datasource.setResource(datasource.connection?.id!!, conn)
            } else {
                conn = resource as Connection
            }

        }
        return conn
    }


}