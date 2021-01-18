package unibz.cs.semint.kprime.domain.schemalgo

import org.junit.Test
import unibz.cs.semint.kprime.domain.ddl.Schema
import unibz.cs.semint.kprime.domain.ddl.schemalgo.oid
import kotlin.test.assertEquals

class OidsTest {

    @Test
    fun test_oid_addition_single_table() {
        // given
        val schema = Schema()
        schema.addTable("person:name,surname,address")
        schema.addTable("teacher:name,surname,course")
        schema.addKey("person:name,surname")
        schema.addDoubleInc("person:name,surname<->teacher:name,surname")
        val originTableName = "person"
        // when
        val sqlCommands = oid(schema, originTableName)
        // then
        assertEquals("ALTER TABLE person ADD COLUMN sid int NOT NULL auto_increment UNIQUE",sqlCommands[0])
        assertEquals("CREATE TABLE SKEYperson AS SELECT sid,surname,name FROM person",sqlCommands[1])
        assertEquals("ALTER TABLE person DROP COLUMN surname,name",sqlCommands[2])
    }
}