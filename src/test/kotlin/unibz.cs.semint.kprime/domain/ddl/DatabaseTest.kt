package unibz.cs.semint.kprime.domain.ddl

import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.scenario.person.PersonTransfomerScenarioTI
import kotlin.test.assertEquals

class DatabaseTest {

    @Test
    fun test_parse_trans1_database() {
        // given
        val dbXml = DatabaseTest::class.java.getResource("/db/trans1/database.xml").readText()
        // when
        val db = XMLSerializerJacksonAdapter().deserializeDatabase(dbXml)
        // then
        assertEquals("",db.id)

    }

}