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
    fun test_qty_mapping_unwrapping() {
        // given
        val db = Database()

        val sqlQuery1 = """
            select activity_id, location_id,  sum(units_completed) as quantity
            from weekly_monitoring wm
            where wm.project_id = 132
            group by activity_id, location_id
          """.trimIndent()

        val weekly_monitoring_quantity = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("weekly_monitoring_quantity", sqlQuery1))

        val sqlQuery2 = """
            select  daily.activity_id, daily.location_id, sum(daily.no_of_units) as quantity
            from abs_daily_monitoring daily
            where daily.project_id = 132
            group by daily.activity_id, daily.location_id     
          """.trimIndent()

        val daily_monitoring_quantity = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("daily_monitoring_quantity", sqlQuery2))


        val sqlQuery3 = """
            select  daily.activity_id, daily.location_id, sum(daily.no_of_units) as quantity
            from abs_daily_monitoring daily
            where daily.project_id = 132
            group by daily.activity_id, daily.location_id
            union
            select activity_id, location_id,  sum(units_completed) as quantity
            from weekly_monitoring wm
            where wm.project_id = 132
            group by activity_id, location_id
          """.trimIndent()
        val qty = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("qty", sqlQuery3))



        println(qty)
        db.mappings().add(weekly_monitoring_quantity)
        db.mappings().add(daily_monitoring_quantity)
        db.mappings().add(qty)
        // when
        val qtyUnwrapped1 = db.mappingUnwrap("weekly_monitoring_quantity").getOrThrow()
        // then
        assertEquals("""
            CREATE OR REPLACE VIEW public.weekly_monitoring_quantity AS
            SELECT activity_id,location_id,sum(units_completed) AS quantity
            FROM   weekly_monitoring wm
            WHERE wm.project_id = 132
            GROUP BY activity_id,location_id
        """.trimIndent(), SQLizeCreateUseCase().createViewCommand(qtyUnwrapped1))

        // when
        val qtyUnwrapped2 = db.mappingUnwrap("daily_monitoring_quantity").getOrThrow()
        // then
        assertEquals("""
            CREATE OR REPLACE VIEW public.daily_monitoring_quantity AS
            SELECT daily.activity_id,daily.location_id,sum(daily.no_of_units) AS quantity
            FROM   abs_daily_monitoring daily
            WHERE daily.project_id = 132
            GROUP BY daily.activity_id,daily.location_id
        """.trimIndent(), SQLizeCreateUseCase().createViewCommand(qtyUnwrapped2))

        // when
        val qtyUnwrapped3 = db.mappingUnwrap("qty").getOrThrow()
        // then
        assertEquals("""
            CREATE OR REPLACE VIEW public.qty AS
            SELECT daily.activity_id,daily.location_id,sum(daily.no_of_units) AS quantity
            FROM   abs_daily_monitoring daily
            WHERE daily.project_id = 132
            GROUP BY daily.activity_id,daily.location_id
            UNION
            SELECT activity_id,location_id,sum(units_completed) AS quantity
            FROM   weekly_monitoring wm
            WHERE wm.project_id = 132
            GROUP BY activity_id,location_id
        """.trimIndent(), SQLizeCreateUseCase().createViewCommand(qtyUnwrapped3))

    }
}