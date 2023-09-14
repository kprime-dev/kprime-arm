package unibz.cs.semint.kprime.domain.db.schemalgo

import org.junit.Ignore
import org.junit.Test
import unibz.cs.semint.kprime.domain.db.Database
import kotlin.test.assertEquals

class AggregatesTest {

    @Test
    fun test_aggregate_with_one_fd() {
        // given
        val db = Database()
        db.schema.addTable("Person:name,surname")
        db.schema.addFunctional("Person:name-->surname")
        // when
        val aggregates = aggregates(db.schema)
        // then
        assertEquals(1,aggregates.size)
        assertEquals("""
            Table(name='', id='', view='', condition='', parent=null, columns=[name, surname], catalog=null, schema=null, source=null)
        """.trimIndent(),aggregates[0].toString())
    }

    @Test
    @Ignore
    fun test_aggregate_with_two_fd() {
        // TODO Expected :2  //Actual   :1
        // given
        val db = Database()
        db.schema.addTable("Person:name,surname,depname,depaddress")
        db.schema.addFunctional("Person:name-->surname")
        db.schema.addFunctional("Person:depname-->depaddress")
        // when
        val aggregates = aggregates(db.schema)
        // then
        assertEquals(2,aggregates.size)
        assertEquals("""
            Table(name='', id='', view='', condition='', parent=null, columns=[name, surname], catalog=null, schema=null, source=null)
        """.trimIndent(),aggregates[0].toString())
        assertEquals("""
            Table(name='', id='', view='', condition='', parent=null, columns=[depname, depaddress], catalog=null, schema=null, source=null)
        """.trimIndent(),aggregates[1].toString())
    }

    @Test
    fun test_aggregate_with_two_fd_chained() {
        // given
        val db = Database()
        db.schema.addTable("Person:name,surname,depname,depaddress")
        db.schema.addFunctional("Person:name-->surname,depname")
        db.schema.addFunctional("Person:depname-->depaddress")
        // when
        val aggregates = aggregates(db.schema)
        // then
        assertEquals(1,aggregates.size)
        assertEquals("""
            Table(name='', id='', view='', condition='', parent=null, columns=[name, surname, depname, depaddress], catalog=null, schema=null, source=null)
        """.trimIndent(),aggregates[0].toString())
    }

    @Test
    fun test_aggregate_projection() {
        // given
        val db  = Database()
        db.schema.addTable("Person:name,surname,phone,depname,depaddress")
        db.schema.addFunctional("Person:name-->surname,phone,depname")
        db.schema.addFunctional("Person:depname-->depaddress")
        db.schema.addFunctional("Person:depaddress-->depname")
        db.schema.table("Person")?.colByName("depname")?.nullable = true
        db.schema.table("Person")?.colByName("depaddress")?.nullable = true
        // when
        // insert as aggregate
        // then
        // insert into original table
    }

}