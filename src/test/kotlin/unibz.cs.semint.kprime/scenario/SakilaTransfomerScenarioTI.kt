package unibz.cs.semint.kprime.scenario

import org.junit.Rule
import org.junit.Test
import unibz.cs.semint.kprime.adapter.repository.QueryJdbcAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.Xrule
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dql.Query
import unibz.cs.semint.kprime.usecase.SQLizeUseCase
import unibz.cs.semint.kprime.usecase.XMLSerializeUseCase
import unibz.cs.semint.kprime.usecase.XPathTransformUseCase
import java.io.StringWriter
import java.util.*

class SakilaTransfomerScenarioTI {

    @Test
    /*
        Executes a query to sakila after split table in views.
        Without changing the database.
     */
    fun test_query_xpath_vertical_decomposition_on_sakila_db() {
        // given
        val dbFilePath = "db/sakila_film_functional.xml"
        val transfomerXml = SakilaTransfomerScenarioTI::class.java.getResource("/transformer/sakilaVTransfomer.xml").readText()
        val vTransfomer = XMLSerializeUseCase(XMLSerializerJacksonAdapter()).deserializeTransformer(transfomerXml).ok
        val templateFilePath = vTransfomer!!.splitter.template.filename
        val xrules = Xrule.toProperties(vTransfomer!!.splitter.xman.xrules)
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["originTable"]="film"
        tranformerParmeters["targetTable1"]="film1"
        tranformerParmeters["targetTable2"]="film2"
        println(templateFilePath)
        // when
        val newdb = XPathTransformUseCase().transform(dbFilePath, templateFilePath, xrules, tranformerParmeters, StringWriter())
        // then
        val simpleQuery = Query.simpleQueryFixture(newdb, "film2")

        val type = "psql"
        val name = "sakila-source"
        val driver = "org.postgresql.Driver"
        val path = "jdbc:postgresql://localhost:5432/sakila"
        val user = System.getenv()["sakila_user"]?:""//"npedot"
        val pass = System.getenv()["sakila_pass"]?:""//"password"
        //val user = "sammy"
        //val pass = "pass"
        val sakilaSource = DataSource(type,name,driver,path,user,pass)

        val result = QueryJdbcAdapter().query(sakilaSource, simpleQuery)
        // print to console output
    }

    @Test
            /*
                Execute a schema manipulation applying changeset to sakila after split table in views.
                Changes the database.
             */
    fun test_create_sakila_film_split_views() {
        // given
        val dbFilePath = "db/sakila_film_functional.xml"
        val transfomerXml = SakilaTransfomerScenarioTI::class.java.getResource("/transformer/sakilaVTransfomer.xml").readText()
        val vTransfomer = XMLSerializeUseCase(XMLSerializerJacksonAdapter()).deserializeTransformer(transfomerXml).ok
        val templateFilePath = vTransfomer!!.splitter.template.filename
        val xrules = Xrule.toProperties(vTransfomer!!.splitter.xman.xrules)
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["originTable"]="film"
        tranformerParmeters["targetTable1"]="film1"
        tranformerParmeters["targetTable2"]="film2"
        println(templateFilePath)
        val newDb = XPathTransformUseCase().transform(dbFilePath, templateFilePath, xrules, tranformerParmeters, StringWriter())
        // create the db views
        val type = "psql"
        val name = "sakila-source"
        val driver = "org.postgresql.Driver"
        val path = "jdbc:postgresql://localhost:5432/sakila"
        val user = System.getenv()["sakila_user"]?:""//"npedot"
        val pass = System.getenv()["sakila_pass"]?:""//"password"
        val sakilaSource = DataSource(type,name,driver,path,user,pass)
        // when
        val createList = SQLizeUseCase().sqlize(newDb)
        for (createCommand in createList) {
            println(createCommand)
            QueryJdbcAdapter().create(sakilaSource, createCommand)
        }
    }



    @Test
    /*  TODO: test_xpath_horizontal_decomposition_on_person_db
        Executes a query to sakila after split horizontal table in views.
        Without changing the database.
     */
    fun test_xpath_horizontal_decomposition_on_person_db() {
        // given
        val dbFilePath = "db/sakila_film_functional.xml"
        val transfomerXml = SakilaTransfomerScenarioTI::class.java.getResource("/transformer/sakilaHTransfomer.xml").readText()
        val vTransfomer = XMLSerializeUseCase(XMLSerializerJacksonAdapter()).deserializeTransformer(transfomerXml).ok
        val templateFilePath = vTransfomer!!.splitter.template.filename
        val xrules = Xrule.toProperties(vTransfomer!!.splitter.xman.xrules)
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["originTable"]="film"
        tranformerParmeters["targetTable1"]="film1"
        tranformerParmeters["targetTable2"]="film2"
        tranformerParmeters["condition"]="select * from film where language_id=2"
        println(templateFilePath)
        // when
        val newDb = XPathTransformUseCase().transform(dbFilePath, templateFilePath, xrules, tranformerParmeters, StringWriter())
        // then
        // print to console output

    }


}