package unibz.cs.semint.kprime.domain.db

import org.junit.Test
import unibz.cs.semint.kprime.domain.dql.Mapping
import unibz.cs.semint.kprime.usecase.common.SQLizeCreateUseCase
import unibz.cs.semint.kprime.usecase.common.UnSQLizeSelectUseCase
import kotlin.test.assertEquals

class DatabaseMappingUnwrapTest {

    @Test
    /*
(
    select  daily.activity_id, daily.location_id, sum(daily.no_of_units) as quantity
    from abs_daily_monitoring daily
    where daily.project_id = 132
    group by daily.activity_id, daily.location_id
union all
    select activity_id, location_id,  sum(units_completed) as quantity
    from weekly_monitoring wm
    where wm.project_id =132
    group by activity_id, location_id
) qty
     */
    fun test_qty() {
        // given
        val db = Database()
        val weekly_monitoring_quantity = Mapping()
        val daily_monitoring_quantity = Mapping()

        val sqlQuery = """
            select activity_id, location_id,  sum(units_completed) as quantity
            from weekly_monitoring wm
            where wm.project_id = 132
            group by activity_id, location_id
          """.trimIndent()

        val qty = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("qty", sqlQuery))
        println(qty)
        db.mappings().add(weekly_monitoring_quantity)
        db.mappings().add(daily_monitoring_quantity)
        db.mappings().add(qty)
        // when
        val qtyUnwrapped = db.mappingUnwrap("qty").getOrThrow()
        // then
        assertEquals("""
            CREATE OR REPLACE VIEW public.qty AS
            SELECT activity_id,location_id,sum(units_completed) AS quantity
            FROM   weekly_monitoring wm
            WHERE wm.project_id = 132
            GROUP BY activity_id,location_id
        """.trimIndent(), SQLizeCreateUseCase().createViewCommand(qtyUnwrapped))
    }
}