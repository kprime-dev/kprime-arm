package unibz.cs.semint.kprime.domain.schemalgo

import org.junit.Test
import unibz.cs.semint.kprime.domain.ddl.Constraint
import unibz.cs.semint.kprime.domain.ddl.Schema
import unibz.cs.semint.kprime.domain.ddl.schemalgo.oid
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
        val sqlCommands = oid(schema, originTableName).sqlCommands!!
        // then
        assertEquals(4,sqlCommands.size)
        assertEquals("ALTER TABLE person ADD COLUMN sidperson int NOT NULL auto_increment UNIQUE",sqlCommands[0])
        assertEquals("CREATE TABLE SKEYperson AS SELECT sidperson,surname,name FROM person",sqlCommands[1])
        // FIXME manca join name, ha preso solo surname
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
    // TODO oid fun using changeset
    fun test_two_oid_four_tables() {
        // given
        val schema = Schema()
//        table1: SSN , Phone
        schema.addTable("ssn_phone:ssn,phone")
        schema.addKey("ssn_phone:ssn,phone")
//        table3: SSN , Name ->double-inc table1
        schema.addTable("ssn_name:ssn,name")
        schema.addKey("ssn_name:ssn")
        schema.addDoubleInc("ssn_phone:ssn<->ssn_name:ssn")
//        table7: SSN , DepName
        schema.addTable("ssn_depname:ssn,depname")
        schema.addInclusion("ssn_depname:ssn-->ssn_name:ssn")
        schema.addInclusion("ssn_depname:ssn-->ssn_phone:ssn")
//        table8: DepName , DepAddress -> double-inc ssn_depname
        schema.addTable("ssn_depaddress:depname,depaddress")
        schema.addKey("ssn_depaddress:depname")
        schema.addDoubleInc("ssn_depaddress:depname<->ssn_depname:depname")
        // when
        val changeset = oid(schema, "ssn_name")
        val sqlCommands = changeset.sqlCommands!!
        // then
        assertEquals(5,sqlCommands.size)
        assertEquals(0,changeset.size())

        val changeset2 = oid(schema, "ssn_depaddress")
        val sqlCommands2 = changeset2.sqlCommands!!
        // then
        assertEquals(4,sqlCommands2.size)
        assertEquals(0,changeset2.size())

        assertEquals(
                "[ssn_phone, ssn_name, ssn_depname, ssn_depaddress, SKEYssn_name, ssn_phone_1, ssn_depname_1, SKEYssn_depaddress, ssn_depname_1_1]",
                schema.tables?.map { it.name }.toString())


        val constraintsSsnPhone1 = schema.constraintsByTable("ssn_phone_1")
        assertEquals(3, constraintsSsnPhone1.size)
//        assertEquals("ssn_phone_ssn_name.doubleInc1:DOUBLE_INCLUSION ssn_phone_1:sidssn_name --> ssn_name:sidssn_name ; ",constraintsSsnPhone1[0].toStringWithName())
        assertEquals("ssn_depname_ssn_phone.inclusion2:INCLUSION ssn_depname_1_1:sidssn_name --> ssn_phone_1:sidssn_name ; ",constraintsSsnPhone1[0].toStringWithName())
        assertEquals("pkey_ssn_phone_1:SURROGATE_KEY ssn_phone_1:sidssn_name --> ssn_phone_1:sidssn_name ; ",constraintsSsnPhone1[1].toStringWithName())
        assertEquals("ssn_phone_ssn_name.doubleInc1_1:DOUBLE_INCLUSION ssn_phone_1:sidssn_name --> SKEYssn_name:sidssn_name ; ",constraintsSsnPhone1[2].toStringWithName()) //TO FIX

        val constraintsSsnDepname11 = schema.constraintsByTable("ssn_depname_1_1")
        assertEquals(5, constraintsSsnDepname11.size)
//        assertEquals("INCLUSION ssn_depname_1_1:sidssn_name --> ssn_name:sidssn_name ; ",constraintsSsnDepname11[0].toString())
        assertEquals("INCLUSION ssn_depname_1_1:sidssn_name --> ssn_phone_1:sidssn_name ; ",constraintsSsnDepname11[0].toString())
//        assertEquals("DOUBLE_INCLUSION ssn_depaddress:sidssn_depaddress --> ssn_depname_1_1:sidssn_depaddress ; ",constraintsSsnDepname11[1].toString())
        assertEquals("SURROGATE_KEY ssn_depname_1_1:sidssn_name --> ssn_depname_1_1:sidssn_name ; ",constraintsSsnDepname11[1].toString())

        assertEquals("ssn_depname_ssn_name.inclusion1_1:INCLUSION ssn_depname_1_1:ssn --> SKEYssn_name:ssn ; ",constraintsSsnDepname11[2].toStringWithName())//TO FIX

        assertEquals("pkey_ssn_depname_1_1:SURROGATE_KEY ssn_depname_1_1:sidssn_depaddress --> ssn_depname_1_1:sidssn_depaddress ; ",constraintsSsnDepname11[3].toStringWithName())

        assertEquals("ssn_depaddress_ssn_depname.doubleInc2_1:DOUBLE_INCLUSION SKEYssn_depaddress:depname --> ssn_depname_1_1:depname ; ",constraintsSsnDepname11[4].toStringWithName())//TO FIX
    }

    @Test
    fun test_one_table() {
        //given
        val schema = Schema()
        schema.addTable("Person:code,name,surname")
        schema.addKey("Person:code")
        // when
        val changeset = oid(schema,"Person")
        // then
        assertNotNull(schema.table("SKEYPerson"))
        val constraints = schema.constraints()
        assertEquals(4, constraints.size)
        assertTrue(constraints.toString().contains("SURROGATE_KEY  Person:sidPerson -->  Person:sidPerson ; "))
        assertTrue(constraints.toString().contains("PRIMARY_KEY SKEYPerson:code --> SKEYPerson:code ; "))
        assertTrue(constraints.toString().contains("SURROGATE_KEY SKEYPerson:sidPerson --> SKEYPerson:sidPerson ; "))
        assertTrue(constraints.toString().contains("DOUBLE_INCLUSION SKEYPerson:sidPerson --> Person:sidPerson ; "))
    }

    @Test
    fun test_three_tables() {
        // given
        val schema = Schema()
    }

}

