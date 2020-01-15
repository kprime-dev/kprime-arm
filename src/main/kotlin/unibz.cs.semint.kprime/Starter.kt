package unibz.cs.semint.kprime

import unibz.cs.semint.kprime.adapter.repository.MetaSchemaJdbcAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.Database
import unibz.cs.semint.kprime.usecase.MetaSchemaReadUseCase

class Starter {


    fun main(args:Array<String>) {
        val version = "0.1.0-SNAPSHOT"
        println("KPrime $version")
        val sakilaMeta = readSakilaMeta()
        if (sakilaMeta!=null) {
            vsplitSakila(sakilaMeta)
            hsplitSakila(sakilaMeta)
            identifySakila(sakilaMeta)
        }
    }


    fun readSakilaMeta():Database? {
        val type = "psql"
        val name = "sakila-source"
        val driver = "org.postgresql.Driver"
        val path = "jdbc:postgresql://localhost:5432/sakila"
        val user = "sammy"
        val pass = "pass"
        val sakilaSource = DataSource(type,name,driver,path,user,pass)
        val result = MetaSchemaReadUseCase().doit(sakilaSource,
                "read-meta-schema sakila-source",
                MetaSchemaJdbcAdapter(),
                XMLSerializerJacksonAdapter())
        return result.ok
    }

    fun vsplitSakila(db:Database) {
        // if detect(db):result
        //  apply(db,result):db
    }

    fun hsplitSakila(db:Database) {
        // if detect(db):result
        //  apply(db,result):db
    }

    fun identifySakila(db:Database) {
        // if detect(db):result
        //  apply(db,result):db
    }

}