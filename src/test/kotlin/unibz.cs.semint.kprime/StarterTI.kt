package unibz.cs.semint.kprime

import org.junit.Test
import org.junit.Assert.*

/**
 * Applies Starter to local Sakila Postgres Example.
 */
class StarterTI {

    @Test
    fun test_starter() {
        // given
        val args = arrayOf<String>()
        // when
        Starter.main(args)
        // then
        assertEquals("Alfa", "Alfa")
    }

}

