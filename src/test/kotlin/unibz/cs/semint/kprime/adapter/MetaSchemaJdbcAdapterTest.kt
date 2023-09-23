package unibz.cs.semint.kprime.adapter

import org.junit.Ignore
import org.junit.Test
import unibz.cs.semint.kprime.adapter.repository.MetaSchemaJdbcAdapter
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.db.Database
import kotlin.test.assertNotNull

class MetaSchemaJdbcAdapterTest {

    @Test
    @Ignore
    fun test_psql_meta() {
        // given
        val adapter = MetaSchemaJdbcAdapter()
        val datasource = DataSource(
            "jdbc","mypg",
            "org.postgresql.Driver",
            "jdbc:postgresql://localhost:5432/sakila",
            "sammy","pass",
            "")
        // when
        val metaDatabase = adapter.metaDatabase(datasource,Database(),"",null,null)
        // then
        assertNotNull(metaDatabase)
        println(metaDatabase)
    }


    @Test
    @Ignore
    fun test_mysql_meta() {
        // given
        val adapter = MetaSchemaJdbcAdapter()
        val datasource = DataSource(
            "jdbc","mysql",
            "com.mysql.cj.jdbc.Driver",
            "jdbc:mysql://localhost:3306/db_monitoring",
            "user","password",
            "")
        // when
        val metaDatabase = adapter.metaDatabase(datasource, Database(),"",null,null)
        // then
        assertNotNull(metaDatabase)
        println(metaDatabase)
    }
}