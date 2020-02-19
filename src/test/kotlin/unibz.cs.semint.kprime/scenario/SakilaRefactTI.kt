package unibz.cs.semint.kprime.scenario

import org.junit.Test
import unibz.cs.semint.kprime.adapter.file.FileIOAdapter
import unibz.cs.semint.kprime.adapter.repository.MetaSchemaJdbcAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.usecase.MetaSchemaReadUseCase
import unibz.cs.semint.kprime.usecase.TransformerVUseCase

/*
	>show-meta sakila-source film
	COLUMNS:
                    NAME      TYPE           SIZE       DEC NULL  INC

                 film_id   INTEGER             10         0   NO  YES
                   title   VARCHAR            255         0   NO   NO
             description   VARCHAR     2147483647         0  YES   NO
            release_year  DISTINCT     2147483647         0  YES   NO
             language_id  SMALLINT              5         0   NO   NO
	original_language_id  SMALLINT              5         0  YES   NO
	     rental_duration  SMALLINT              5         0   NO   NO
             rental_rate   NUMERIC              4         2   NO   NO
                  length  SMALLINT              5         0  YES   NO
	    replacement_cost   NUMERIC              5         2   NO   NO
                  rating   VARCHAR     2147483647         0  YES   NO
             last_update TIMESTAMP             29         6   NO   NO
	    special_features     ARRAY     2147483647         0  YES   NO
    		    fulltext     OTHER     2147483647         0   NO   NO
	PRIMARY:
	   film_id === film_pkey
	FOREIGN:
	   language --- language_id === language_id
	   language --- language_id === original_language_id


 */
class SakilaRefactTI {

    @Test
    fun test_api() {
        var db = readMeta(sakilaDataSource())
        if (db==null) {
            println("sakila meta db not open")
            return
        }
        db.schema
                //.checkBcnf()
                .addFunctionals("film","film_id --> replacement_cost, rental_duration, rental_rate")

        db = TransformerVUseCase(XMLSerializerJacksonAdapter(),FileIOAdapter()).decompose(
                db,"film","film_catalog","film_rental")		// detect lossy
//                .hdecompose("film_core","film_ita","film_not_ita","select * where language='IT'") // detect lossy
//                .genarm()
//                .alias("film_ita","film_italiani")
//                .rename("length","lunghezza")
//                .query("select * from film_italiani where lunghezza<30")
//                .browse("select * join ")
//                .export("select * from film_italiani where lunghezza<30")
//                .drop("select *")
//                .map("data formatter")
//                .sqlrefact()

    }

    private fun readMeta(sakilaSource: DataSource): Database? {
        val result = MetaSchemaReadUseCase().doit(sakilaSource,
                "read-meta-schema sakila-source",
                MetaSchemaJdbcAdapter(),
                XMLSerializerJacksonAdapter())
        return result.ok
    }

    private fun sakilaDataSource(): DataSource {
        val user = System.getenv()["sakila_user"] ?: ""//"npedot"
        val pass = System.getenv()["sakila_pass"] ?: ""//"password"
        val sakilaSource = DataSource(
                "psql",
                "sakila-source",
                "org.postgresql.Driver",
                "jdbc:postgresql://localhost:5432/sakila", user, pass)
        return sakilaSource
    }

}