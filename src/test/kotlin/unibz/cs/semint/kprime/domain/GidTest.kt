package unibz.cs.semint.kprime.domain

import org.junit.Test
import java.util.*
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GidTest {

    @Test
    fun test_gid() {
        assertTrue("1234567890".isValidGid())
        assertFalse("".isValidGid())
        assertFalse("123456789".isValidGid())
        // UUID 9c63265d-f068-46d4-83fb-48d10c6ea8d8
        assertTrue(UUID.randomUUID().toString().isValidGid())
        assertTrue(nextGid().isValidGid())
        assertTrue("9c63265d-f068-46d4-83fb-48d10c6ea8d8".isValidGid())
        assertFalse("9c63265d-f068-46d4-83fb+48d10c6ea8d8".isValidGid())
        assertFalse("9c63265d%f068-46d4-83fb-48d10c6ea8d8".isValidGid())
    }
}