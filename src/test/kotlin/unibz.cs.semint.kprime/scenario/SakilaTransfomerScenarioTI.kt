package unibz.cs.semint.kprime.scenario

import org.junit.Rule
import org.junit.Test
import unibz.cs.semint.kprime.adapter.repository.QueryJdbcAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.Xrule
import unibz.cs.semint.kprime.domain.dql.Query
import unibz.cs.semint.kprime.usecase.XMLSerializeUseCase
import unibz.cs.semint.kprime.usecase.XPathTransformUseCase
import java.io.StringWriter
import java.util.*

class SakilaTransfomerScenarioTI {

    @Test
    fun test_xpath_vertical_decomposition_on_sakila_db() {
        // given
        val dbFilePath = "db/sakila_film_functional.xml"
        val transfomerXml = SakilaTransfomerScenarioTI::class.java.getResource("/transformer/sakilaVTransfomer.xml").readText()
        val vTransfomer = XMLSerializeUseCase(XMLSerializerJacksonAdapter()).deserializeTransformer(transfomerXml).ok
        val templateFilePath = vTransfomer!!.splitter.template.filename
        val xrules = toProperties(vTransfomer!!.splitter.xman.xrules)
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["table"]="film"
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

    private fun toProperties(xrules: ArrayList<Xrule>): Properties {
        var pros = Properties()
        for (xrule in xrules) {
            pros[xrule.name]=xrule.rule
        }
        return pros
    }

    @Test
    fun test_xpath_horizontal_decomposition_on_person_db() {
        /*
        // given
        val dbFilePath = "db/person.xml"
        val trasformerName = "horizontal"
        val trasformerDirection = "decompose"
        val trasformerVersion = "1"
        val tranformerParmeters = mutableMapOf<String,Any>()
        tranformerParmeters["table"]="person"
        // when
        XPathTransformUseCase().transform(dbFilePath, trasformerName, trasformerDirection, trasformerVersion,tranformerParmeters,OutputStreamWriter(System.out))
        // then
        // print to console output
         */
    }


}