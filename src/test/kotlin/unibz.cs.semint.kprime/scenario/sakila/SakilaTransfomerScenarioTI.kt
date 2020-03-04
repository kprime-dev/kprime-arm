package unibz.cs.semint.kprime.scenario.sakila

import org.junit.Test
import unibz.cs.semint.kprime.adapter.repository.QueryJdbcAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.Xrule
import unibz.cs.semint.kprime.domain.dql.Query
import unibz.cs.semint.kprime.usecase.common.SQLizeUseCase
import unibz.cs.semint.kprime.usecase.common.XMLSerializeUseCase
import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase
import java.io.StringWriter

class SakilaTransfomerScenarioTI {

    @Test
/*
    Executes a query to sakila after split table in views.
    Without changing the database.
 */
    fun test_query_xpath_vertical_decomposition_on_sakila_db() {
        // given
        val dbFilePath = "db/sakila_film_functional.xml"
        val transfomerXml = SakilaTransfomerScenarioTI::class.java.getResource("/transformer/verticalTransfomer.xml").readText()
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
        val simpleQueryTab = Query.buildFromTable(newdb, "film2")
        val simpleQueryMap1 = Query.buildFromMapping(newdb, "film1")!!
        val simpleQueryMap2 = Query.buildFromMapping(newdb, "film2")!!

        val type = "psql"
        val name = "sakila-source"
        val driver = "org.postgresql.Driver"
        val path = "jdbc:postgresql://localhost:5432/sakila"
        val user = System.getenv()["sakila_user"]?:""//"npedot"
        val pass = System.getenv()["sakila_pass"]?:""//"password"
        //val user = "sammy"
        //val pass = "pass"
        val sakilaSource = DataSource(type,name,driver,path,user,pass)

        val sqlize = SQLizeUseCase().sqlize(simpleQueryMap1)
        println(sqlize)
        val result1 = QueryJdbcAdapter().query(sakilaSource, simpleQueryMap1)
        val result2 = QueryJdbcAdapter().query(sakilaSource, simpleQueryMap2)
        // print to console output
    }

    @Test
/*
    Execute a schema manipulation applying changeset to sakila after split table in views.
    WARNING Changes the database.
 */
    fun test_create_sakila_film_split_views() {
        // given
        val dbFilePath = "db/sakila_film_functional.xml"
        val transfomerXml = SakilaTransfomerScenarioTI::class.java.getResource("/transformer/verticalTransfomer.xml").readText()
        val vTransfomer = XMLSerializeUseCase(XMLSerializerJacksonAdapter()).deserializeTransformer(transfomerXml).ok
        val templateFilePath = vTransfomer!!.splitter.template.filename
        val xrules = Xrule.toProperties(vTransfomer!!.splitter.xman.xrules)
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["originTable"]="film"
        tranformerParmeters["targetTable1"]="film_core"
        tranformerParmeters["targetTable2"]="film_nullable"
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
        val createList = SQLizeUseCase().createViewCommands(newDb)
        for (createCommand in createList) {
            println(createCommand)
            QueryJdbcAdapter().create(sakilaSource, createCommand)
        }
    }



    @Test
    /*
        Executes a query to sakila after split horizontal table in views.
        Without changing the database.
     */
    // TODO work in progress...
    fun test_xpath_horizontal_decomposition_on_person_db() {
        // given
        val dbFilePath = "db/sakila_film_functional.xml"
        val transfomerXml = SakilaTransfomerScenarioTI::class.java.getResource("/transformer/horizontalTransfomer.xml").readText()
        val vTransfomer = XMLSerializeUseCase(XMLSerializerJacksonAdapter()).deserializeTransformer(transfomerXml).ok
        val templateFilePath = vTransfomer!!.splitter.template.filename
        val xrules = Xrule.toProperties(vTransfomer!!.splitter.xman.xrules)
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["originTable"]="film"
        tranformerParmeters["targetTable1"]="film_italiano"
        tranformerParmeters["targetTable2"]="film_non_italiano"
        tranformerParmeters["condition"]="select * from film where language_id=2"
        println(templateFilePath)
        // when
        val newDb = XPathTransformUseCase().transform(dbFilePath, templateFilePath, xrules, tranformerParmeters, StringWriter())
        // then
        // print to console output

    }


}