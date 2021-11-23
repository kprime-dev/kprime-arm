package unibz.cs.semint.kprime.usecase.common

import org.junit.Test
import kotlin.test.assertEquals

class UnSQLizeSelectUseCaseTest {

    @Test
    fun split_on_keywords_2_rows() {
        // given
        val case = UnSQLizeSelectUseCase()
        // when
        val splitResult = case.splitOnKeyWords("SELECT * FROM people")
        // then
//        splitResult.forEach{ println("'$it'") }
        assertEquals(2, splitResult.size)
        assertEquals("SELECT *", splitResult[0])
        assertEquals("FROM people", splitResult[1])
    }

    @Test
    fun split_on_keywords_3_rows() {
        // given
        val case = UnSQLizeSelectUseCase()
        // when
        val splitResult = case.splitOnKeyWords("SELECT a,b FROM people WHERE a<b")
        // then
//        splitResult.forEach{ println("'$it'") }
        assertEquals(3, splitResult.size)
        assertEquals("SELECT a,b", splitResult[0])
        assertEquals("FROM people", splitResult[1])
        assertEquals("WHERE a<b", splitResult[2])
    }

    @Test
    fun split_on_keywords_7_rows() {
        // given
        val case = UnSQLizeSelectUseCase()
        // when
        val splitResult = case.splitOnKeyWords("SELECT a,b FROM people WHERE a<b UNION SELECT c,d FROM jobs WHERE c<d ")
        // then
//        splitResult.forEach{ println("'$it'") }
        assertEquals(7, splitResult.size)
        assertEquals("SELECT a,b", splitResult[0])
        assertEquals("FROM people", splitResult[1])
        assertEquals("WHERE a<b", splitResult[2])
    }

}