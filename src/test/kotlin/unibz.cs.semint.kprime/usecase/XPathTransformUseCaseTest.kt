package unibz.cs.semint.kprime.usecase

import org.junit.Ignore
import org.junit.Test
import unibz.cs.semint.kprime.usecase.common.XPathTransformUseCase

class XPathTransformUseCaseTest {


    @Test
    fun test_compute_vertical_person() {
        val xtransf = XPathTransformUseCase()
        //when
        val dbFilePath = "/home/nipe/Workspaces/semint-kprime/src/test/resources/db/person.xml"
        val templateFilePath = "/home/nipe/Workspaces/semint-kprime/src/test/resources/transformer/vertical/decompose/vertical_changeset_1.template"
        val xPaths= """
originTable==/database/schema/constraints/constraints[@type='FUNCTIONAL']/source/@table > 0
all==/database/schema/tables/tables[@name='originTable']/columns/columns/@name
keys==/database/schema/constraints/constraints[@type='PRIMARY_KEY']/source[@table='originTable']/columns/columns/@name
lhss==/database/schema/constraints/constraints[@type='FUNCTIONAL']/source[@table='originTable']/columns/columns/@name > 0
rhss==/database/schema/constraints/constraints[@type='FUNCTIONAL']/target[@table='originTable']/columns/columns/@name > 0
rests==- all keys lhss rhss
table==/database/schema/tables/tables[@name='originTable']/@name
view1==+ keys lhss rests
view2==+ lhss rhss            
        """.trimIndent().split(System.lineSeparator())
        val tranformerParmeters = mutableMapOf<String,Any>()
        val computed = xtransf.compute(dbFilePath, templateFilePath, xPaths, tranformerParmeters)
        // then
        print(computed)
    }

    @Test
    fun test_compute_horizontal_person() {
        val xtransf = XPathTransformUseCase()
        //when
        val dbFilePath = "/home/nipe/Workspaces/semint-kprime/src/test/resources/db/person.xml"
        val templateFilePath = "/home/nipe/Workspaces/semint-kprime/src/test/resources/transformer/horizontal/decompose/horizontal_changeset_1.template"
        val xPaths= listOf(
            "((originTable)),((condition))",
            "all==/database/schema/tables/tables[@name='originTable'][1]/columns/columns/@name"
        )
        val tranformerParmeters = mutableMapOf<String,Any>(
                "condition" to listOf("id=1234"),
                "originTable" to listOf("person")
        )
        val computed = xtransf.compute(dbFilePath, templateFilePath, xPaths, tranformerParmeters)
        // then
    }
}