package unibz.cs.semint.kprime.domain.db

import org.junit.Test
import unibz.cs.semint.kprime.domain.dql.Mapping
import unibz.cs.semint.kprime.usecase.common.SQLizeSelectUseCase
import unibz.cs.semint.kprime.usecase.common.UnSQLizeSelectUseCase
import kotlin.test.assertEquals

class DatabaseMappingUnwrapTest {

    @Test
    fun test_unsql() {
        val sqlQuery4 = """
                SELECT qty.activity_id, qty.location_id, sum(quantity) as quantity
                FROM SELECT q1
                where a=b
                GROUP by qty.activity_id, qty.location_id
                   """.trimIndent()
        val q4 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("q4", sqlQuery4))
        val sqlized = (SQLizeSelectUseCase().sqlize(q4))
        assertEquals("""
            SELECT q1
            WHERE a=b
            GROUP BY qty.activity_id,qty.location_id
        """.trimIndent(),sqlized)
    }

    @Test
    fun test_mapping_unwrapping_man_hour_spent_by_location() {
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
        val qty = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("qty1", sqlQuery3))


        val sqlQuery4 = """
            SELECT (daily_monitoring_quantity)
            union
            SELECT (weekly_monitoring_quantity)
        """.trimIndent()
        val qty2 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("qty", sqlQuery4))


        println(qty)
        db.mappings().add(weekly_monitoring_quantity)
        db.mappings().add(daily_monitoring_quantity)
        db.mappings().add(qty)
        db.mappings().add(qty2)
        // when
        val qtyUnwrapped1 = db.mappingSql("weekly_monitoring_quantity").getOrThrow()
        // then
        assertEquals("""
            SELECT activity_id,location_id,sum(units_completed) AS quantity
            FROM   weekly_monitoring wm
            WHERE wm.project_id = 132
            GROUP BY activity_id,location_id
        """.trimIndent(), qtyUnwrapped1)

        // when
        val qtyUnwrapped2 = db.mappingSql("daily_monitoring_quantity").getOrThrow()
        // then
        assertEquals("""
            SELECT daily.activity_id,daily.location_id,sum(daily.no_of_units) AS quantity
            FROM   abs_daily_monitoring daily
            WHERE daily.project_id = 132
            GROUP BY daily.activity_id,daily.location_id
        """.trimIndent(), qtyUnwrapped2)

        // when
        val qtyUnwrapped3 = db.mappingSql("qty1").getOrThrow()
        // then
        assertEquals("""
            SELECT daily.activity_id,daily.location_id,sum(daily.no_of_units) AS quantity
            FROM   abs_daily_monitoring daily
            WHERE daily.project_id = 132
            GROUP BY daily.activity_id,daily.location_id
            UNION
            SELECT activity_id,location_id,sum(units_completed) AS quantity
            FROM   weekly_monitoring wm
            WHERE wm.project_id = 132
            GROUP BY activity_id,location_id
        """.trimIndent(), qtyUnwrapped3)

        // when
        val qtyUnwrapped4 = db.mappingSql("qty").getOrThrow()
        // then
        assertEquals("""
            (SELECT daily.activity_id,daily.location_id,sum(daily.no_of_units) AS quantity
            FROM   abs_daily_monitoring daily
            WHERE daily.project_id = 132
            GROUP BY daily.activity_id,daily.location_id)
            UNION
            (SELECT activity_id,location_id,sum(units_completed) AS quantity
            FROM   weekly_monitoring wm
            WHERE wm.project_id = 132
            GROUP BY activity_id,location_id)
        """.trimIndent(), qtyUnwrapped4)


        // given
        // SELECT qty.activity_id, qty.location_id, sum(quantity) as quantity
        val sqlQuery5 = """
            SELECT qty.activity_id, qty.location_id, sum(quantity) as quantity
            FROM (qty)
            GROUP by qty.activity_id, qty.location_id
        """.trimIndent()
        val qty3 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("act_location", sqlQuery5))
        db.mappings().add(qty3)
        // when
        val qtyUnwrapped5 = db.mappingSql("act_location").getOrThrow()
        // then
        assertEquals("""
            SELECT qty.activity_id,qty.location_id,sum(quantity) AS quantity
            FROM   ((SELECT daily.activity_id,daily.location_id,sum(daily.no_of_units) AS quantity
            FROM   abs_daily_monitoring daily
            WHERE daily.project_id = 132
            GROUP BY daily.activity_id,daily.location_id)
            UNION
            (SELECT activity_id,location_id,sum(units_completed) AS quantity
            FROM   weekly_monitoring wm
            WHERE wm.project_id = 132
            GROUP BY activity_id,location_id))  qty
            GROUP BY qty.activity_id,qty.location_id
        """.trimIndent(), qtyUnwrapped5)

        val sqlQuery_budget_activity_location = """
            select act_location.activity_id, act_location.location_id, act_location.quantity,p.crew_size, p.hourly_pitch, (quantity/p.hourly_pitch*p.crew_size) as man_hours
            FROM (act_location),ft_pitch p
            WHERE p.project_id = 132 and p.activity_id = act_location.activity_id
        """.trimIndent()
        val qty_budget_activity_location = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("budget_activity_location", sqlQuery_budget_activity_location))
        db.mappings().add(qty_budget_activity_location)
        // when
        val sqlUnwrapped_budget_activity_location = db.mappingSql("budget_activity_location").getOrThrow()
        // then
        assertEquals("""
            SELECT act_location.activity_id,act_location.location_id,act_location.quantity,p.crew_size,p.hourly_pitch,(quantity/p.hourly_pitch*p.crew_size) AS man_hours
            FROM   (SELECT qty.activity_id,qty.location_id,sum(quantity) AS quantity
            FROM   ((SELECT daily.activity_id,daily.location_id,sum(daily.no_of_units) AS quantity
            FROM   abs_daily_monitoring daily
            WHERE daily.project_id = 132
            GROUP BY daily.activity_id,daily.location_id)
            UNION
            (SELECT activity_id,location_id,sum(units_completed) AS quantity
            FROM   weekly_monitoring wm
            WHERE wm.project_id = 132
            GROUP BY activity_id,location_id))  qty
            GROUP BY qty.activity_id,qty.location_id)  act_location,ft_pitch p
            WHERE p.project_id = 132 and p.activity_id = act_location.activity_id
        """.trimIndent(),sqlUnwrapped_budget_activity_location)

        //given
        val sqlQuery_man_hour_spent_by_location = """
            select budget_activity_location.location_id, sum(budget_activity_location.man_hours)
            from (budget_activity_location)
            group by budget_activity_location.location_id;
        """.trimIndent()
        val mapping_man_hour_spent_by_location = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("man_hour_spent_by_location", sqlQuery_man_hour_spent_by_location))
        db.mappings().add(mapping_man_hour_spent_by_location)
        // when
        val sqlUnwrapped_man_hour_spent_by_location = db.mappingSql("man_hour_spent_by_location").getOrThrow()
        // then
        assertEquals("""
            SELECT budget_activity_location.location_id,sum(budget_activity_location.man_hours)
            FROM   (SELECT act_location.activity_id,act_location.location_id,act_location.quantity,p.crew_size,p.hourly_pitch,(quantity/p.hourly_pitch*p.crew_size) AS man_hours
            FROM   (SELECT qty.activity_id,qty.location_id,sum(quantity) AS quantity
            FROM   ((SELECT daily.activity_id,daily.location_id,sum(daily.no_of_units) AS quantity
            FROM   abs_daily_monitoring daily
            WHERE daily.project_id = 132
            GROUP BY daily.activity_id,daily.location_id)
            UNION
            (SELECT activity_id,location_id,sum(units_completed) AS quantity
            FROM   weekly_monitoring wm
            WHERE wm.project_id = 132
            GROUP BY activity_id,location_id))  qty
            GROUP BY qty.activity_id,qty.location_id)  act_location,ft_pitch p
            WHERE p.project_id = 132 and p.activity_id = act_location.activity_id)  budget_activity_location
            GROUP BY budget_activity_location.location_id;
        """.trimIndent(), sqlUnwrapped_man_hour_spent_by_location)

    }


        @Test
        fun test_simple_unwrap() {
                // given
                val db = Database()

                val sqlQuery1 = """
                    select a1
                    from abc
                    """.trimIndent()
                val q1 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("q1", sqlQuery1))
                db.mappings().add(q1)

                val sqlQuery2 = """
                    select a2
                    from def
                    """.trimIndent()
                val q2 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("q2", sqlQuery2))
                db.mappings().add(q2)

                val sqlQuery3 = """
                        select (q1)
                        union
                        select (q2)        
                """.trimIndent()
                val q3 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("q3", sqlQuery3))
                db.mappings().add(q3)

               // when
                val qtyUnwrapped = db.mappingSql("q3").getOrThrow()
                // then
                assertEquals("""
                    (SELECT a1
                    FROM   abc)
                    UNION
                    (SELECT a2
                    FROM   def)
                """.trimIndent(),qtyUnwrapped)
                println("qtyUnwrapped: $qtyUnwrapped")

        }

        @Test
        fun test_simple_unwrap_from_select() {
                // given
                val db = Database()

                val sqlQuery1 = """
            select a1
            from abc
            """.trimIndent()
                val q1 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("q1", sqlQuery1))
                db.mappings().add(q1)

                val sqlQuery4 = """
                    SELECT a
                    FROM (q1)
                    GROUP by b
                   """.trimIndent()
                val q4 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("q4", sqlQuery4))
                db.mappings().add(q4)

                // when
                val qtyUnwrapped = db.mappingSql("q4").getOrThrow()
                // then
                assertEquals("""
                    SELECT a
                    FROM   (SELECT a1
                    FROM   abc)  q1
                    GROUP BY b
        """.trimIndent(),qtyUnwrapped)
        }

    @Test
    fun test_nested_unwrap_from_select() {
        // given
        val db = Database()

        val sqlQuery1 = """
            select a1
            from abc
            """.trimIndent()
        val q1 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("q1", sqlQuery1))
        db.mappings().add(q1)

        val sqlQuery2 = """
            select a2
            from (q1)
            """.trimIndent()
        val q2 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("q2", sqlQuery2))
        db.mappings().add(q2)

        val sqlQuery4 = """
                    SELECT a
                    FROM (q2)
                    GROUP by b
                   """.trimIndent()
        val q4 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("q4", sqlQuery4))
        db.mappings().add(q4)

        // when
        val qtyUnwrapped = db.mappingSql("q4").getOrThrow()
        // then
        assertEquals("""
            SELECT a
            FROM   (SELECT a2
            FROM   (SELECT a1
            FROM   abc)  q1)  q2
            GROUP BY b
        """.trimIndent(),qtyUnwrapped)
    }

    @Test
    fun test_unwrap_real_query() {
        val q4 = """
                    SELECT qty.activity_id, qty.location_id, sum(quantity) as quantity
                    FROM (qty2)
                    GROUP by qty.activity_id, qty.location_id
                   """.trimIndent()


    }

}