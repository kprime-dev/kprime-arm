package unibz.cs.semint.kprime.scenario

import org.junit.Test
import unibz.cs.semint.kprime.adapter.repository.MetaSchemaJdbcAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.usecase.MetaSchemaReadUseCase

class SakilaScenarioTI {

    @Test
    fun test_sakila_scenario() {
        // given
        val sakilaScenario = SakilaScenarioTI()
        // when
        sakilaScenario.run()
        // then
        // prints manipulated sakila database
    }

    fun run() {
        val sakilaMeta = readSakilaMeta()
        if (sakilaMeta!=null) {
            vsplitSakila(sakilaMeta)
            hsplitSakila(sakilaMeta)
            identifySakila(sakilaMeta)
        }
    }

    private fun readSakilaMeta(): Database? {
        val type = "psql"
        val name = "sakila-source"
        val driver = "org.postgresql.Driver"
        val path = "jdbc:postgresql://localhost:5432/sakila"
        //val user = "sammy"
        val user = "npedot"
        //val pass = "pass"
        val pass = "password"
        val sakilaSource = DataSource(type,name,driver,path,user,pass)
        val result = MetaSchemaReadUseCase().doit(sakilaSource,
                "read-meta-schema sakila-source",
                MetaSchemaJdbcAdapter(),
                XMLSerializerJacksonAdapter())
        return result.ok
    }

    private fun vsplitSakila(db: Database) {
        // if detect(db):result
        //  apply(db,result):db
    }

    private fun hsplitSakila(db: Database) {
        // if detect(db):result
        //  apply(db,result):db
    }

    private fun identifySakila(db: Database) {
        // if detect(db):result
        //  apply(db,result):db
    }

}