package unibz.cs.semint.kprime.usecase.serialize

import com.thoughtworks.xstream.XStream
import org.junit.Test
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.ddl.Database
import kotlin.test.assertEquals

class XStreamTest {

    @Test
    fun test_xstream_db_serialize() {
        // given
        val db = Database()
        val xstream = XStream()
        xstream.alias("database",Database().javaClass)
        // when
        val dbxml = xstream.toXML(db)
        // then
        assertEquals("""
            <database>
              <name></name>
              <id></id>
              <schema>
                <name></name>
                <id></id>
                <tables/>
                <constraints/>
              </schema>
              <mappings/>
            </database>
        """.trimIndent(),dbxml)
    }

    @Test
    fun test_xstream_deserialize() {
        val dbxml = """
            <database name="">
              <id></id>
              <schema>
                <name></name>
                <id></id>
                <tables/>
                <constraints/>
              </schema>
              <mappings/>
            </database>
        """
        // when
        val xstream = XStream()
        xstream.alias("database",Database().javaClass)
        val db: Database = xstream.fromXML(dbxml) as Database
        // then
        val prettyDatabase = XMLSerializerJacksonAdapter().prettyDatabase(db)
        println(prettyDatabase)
        assertEquals(db.schema.tables().size,0)
    }
}