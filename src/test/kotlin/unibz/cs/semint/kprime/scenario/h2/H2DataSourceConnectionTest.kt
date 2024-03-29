package unibz.cs.semint.kprime.scenario.h2

import org.junit.Test
import unibz.cs.semint.kprime.adapter.repository.JdbcAdapter
import unibz.cs.semint.kprime.adapter.repository.JdbcPrinter
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.datasource.DataSourceConnection

class H2DataSourceConnectionTest {

    @Test
    fun test_h2_connection_using_data_source_config() {
        // given
        val dataSourceConnection = DataSourceConnection("test","sa","",true,true,false)
        val dataSource = DataSource("h2", "testdb", "org.h2.Driver", "jdbc:h2:mem:test_mem", "sa", "")
        dataSource.connection = dataSourceConnection
        val sqlAdapter = JdbcAdapter()
        // when
        sqlAdapter.create(dataSource,"CREATE TABLE Person(name varchar(64),surname varchar(64))")
        sqlAdapter.create(dataSource, "INSERT INTO Person VALUES('Gino','Rossi')")
        sqlAdapter.create(dataSource, "INSERT INTO Person VALUES('Gino','Rossi')")
        dataSourceConnection.closed = true
        sqlAdapter.query (dataSource, "SELECT * FROM Person", JdbcPrinter::printJsonResultList)
        // then
    }
}