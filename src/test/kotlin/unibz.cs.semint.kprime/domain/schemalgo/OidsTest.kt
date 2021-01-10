package unibz.cs.semint.kprime.domain.schemalgo

import org.junit.Test
import unibz.cs.semint.kprime.domain.ddl.Schema
import unibz.cs.semint.kprime.domain.ddl.schemalgo.oid

class OidsTest {

    @Test
    fun test_oid_addition_single_table() {
        // given
        val schema = Schema()
        val originTableName = ""
        // when
        oid(schema,originTableName)
    }
}