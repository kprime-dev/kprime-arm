package unibz.cs.semint.kprime.domain.ddl

import org.junit.Test
import kotlin.test.assertEquals

class LabellerTest {

    @Test
    fun test_add_label() {
       // given
       val labeller = Labeller()
       // when
        labeller.addLabels("lab1,lab2")
        labeller.addLabels("lab3,lab4")
        // then
        assertEquals("lab1,lab2,lab3,lab4",labeller.labelsAsString())
    }

    @Test
    fun test_remove_label() {
        // given
        val labeller = Labeller()
        labeller.addLabels("lab1,lab2,lab3")
        // when
        labeller.remLabels("lab2")
        // then
        assertEquals("lab1,lab3",labeller.labelsAsString())
    }

    @Test
    fun test_list_label() {
        // given
        val labeller = Labeller()
        labeller.addLabels("lab1,lab2,lab3")
        // when
        val list = labeller.listLabels()
        // then
        assertEquals("lab1",list[0])
        assertEquals("lab2",list[1])
        assertEquals("lab3",list[2])
    }

    @Test
    fun test_list_label_with_prefix() {
        // given
        val labeller = Labeller()
        labeller.addLabels("lab1,lab2,lab2:a,lab2:b,lab3")
        // when
        val list = labeller.prefixedLabels("lab2")
        // then
        assertEquals(3,list.size)
        assertEquals("lab2",list[0])
        assertEquals("lab2:a",list[1])
        assertEquals("lab2:b",list[2])
    }

}