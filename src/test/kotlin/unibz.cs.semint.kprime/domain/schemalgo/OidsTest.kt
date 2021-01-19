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
        assertEquals("ALTER TABLE person ADD COLUMN sidperson int NOT NULL auto_increment UNIQUE",sqlCommands[0])
        assertEquals("CREATE TABLE SKEYperson AS SELECT sidperson,surname,name FROM person",sqlCommands[1])
        assertEquals("CREATE TABLE teacher_1 AS SELECT SKEYperson.sidperson,teacher.course FROM SKEYperson JOIN teacher ON SKEYperson.surname = teacher.surname",sqlCommands[2])
        assertEquals("ALTER TABLE person DROP COLUMN surname,name",sqlCommands[3])
    }

    @Test
    /*
Tables:
table1: SSN , Phone
table3: SSN , Name
table7: SSN , DepName
table8: DepName , DepAddress

 pkey_table1  PRIMARY_KEY  SSN , Phone  ->  SSN , Phone
 pkey_table3  PRIMARY_KEY  SSN  ->  SSN
 table1_table3.DOUBLE_INCLUSION7  DOUBLE_INCLUSION  SSN  ->  SSN
 pkey_table7  PRIMARY_KEY  SSN  ->  SSN
 pkey_table8  PRIMARY_KEY  DepName  ->  DepName
 table7_table3.INCLUSION12  INCLUSION  SSN  ->  SSN
 table7_table1.INCLUSION13  INCLUSION  SSN  ->  SSN
 table7_table8.DOUBLE_INCLUSION14  DOUBLE_INCLUSION  DepName  ->  DepName
     */
    fun test_oid_two_tables() {
        // given
        val schema = Schema()
//        table1: SSN , Phone
        schema.addTable("table1:ssn,phone")
//        table3: SSN , Name ->double-inc table1
        schema.addTable("table3:ssn,name")
        schema.addKey("table3:ssn")
        schema.addDoubleInc("table1:ssn<->table3:ssn")
//        table7: SSN , DepName
        schema.addTable("table7:ssn,depname")
        schema.addInclusion("table7:ssn-->table3:ssn")
        schema.addInclusion("table7:ssn-->table1:ssn")
//        table8: DepName , DepAddress -> double-inc table7
        schema.addTable("table8:depname,depaddress")
        schema.addDoubleInc("table8:depname<->table7:depname")
        // when
        val sqlCommands = oid(schema,"table3")
        // then
        assertEquals(5,sqlCommands.size)
    }
}

