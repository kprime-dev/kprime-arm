package unibz.cs.semint.kprime.domain.db

import org.junit.Ignore
import org.junit.Test
import unibz.cs.semint.kprime.domain.dql.Mapping
import unibz.cs.semint.kprime.usecase.common.SQLizeCreateUseCase
import unibz.cs.semint.kprime.usecase.common.SQLizeSelectUseCase
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
    @Ignore
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


        val sqlQuery4 = """
            SELECT daily_monitoring_quantity
            union
            SELECT weekly_monitoring_quantity
        """.trimIndent()
        val qty2 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("qty2", sqlQuery4))


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
        val qtyUnwrapped3 = db.mappingSql("qty").getOrThrow()
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
        val qtyUnwrapped4 = db.mappingSql2("qty2").getOrThrow()
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
        """.trimIndent(), qtyUnwrapped4)


        // given
        val sqlQuery5 = """
            SELECT qty.activity_id, qty.location_id, sum(quantity) as quantity
            FROM SELECT qty2
            GROUP by qty.activity_id, qty.location_id
        """.trimIndent()
        val qty3 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("qty3", sqlQuery5))
        db.mappings().add(qty3)
        // when
        val qtyUnwrapped5 = db.mappingSql2("qty3").getOrThrow()
        // then
        /* TO FIX
        */

            /*

    SELECT qty

    GROUP BY qty.activity_id,qty.location_id

            wrong SELECT attributes parse
            wrong FROM parse => empty line

             */
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
        """.trimIndent(), qtyUnwrapped5)
        /*
        */

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
                        SELECT q1
                        union
                        SELECT q2        
                """.trimIndent()
                val q3 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("q3", sqlQuery3))
                db.mappings().add(q3)

                val q4 = """
                    SELECT qty.activity_id, qty.location_id, sum(quantity) as quantity
                    FROM SELECT qty2
                    GROUP by qty.activity_id, qty.location_id
                   """.trimIndent()

               // when
                val qtyUnwrapped = db.mappingSql2("q3").getOrThrow()
                // then
                assertEquals("""
                        SELECT a1
                        FROM   abc
                        UNION
                        SELECT a2
                        FROM   def
                """.trimIndent(),qtyUnwrapped)
                println("qtyUnwrapped: $qtyUnwrapped")

        }

        @Test
        @Ignore
        fun test_simple_unwrap_from_select() {
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

                val sqlQuery4 = """
                    SELECT qty.activity_id, qty.location_id, sum(quantity) as quantity
                    FROM SELECT q1
                    GROUP by qty.activity_id, qty.location_id
                   """.trimIndent()
                val q4 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("q4", sqlQuery4))
                db.mappings().add(q4)

                // when
                val qtyUnwrapped = db.mappingSql2("q4").getOrThrow()
                // then
                assertEquals("""
                SELECT a1
                FROM   abc
                UNION
                SELECT a2
                FROM   def
        """.trimIndent(),qtyUnwrapped)
                println("qtyUnwrapped: $qtyUnwrapped")

        }

        @Test
        fun test_unsql() {
                val sqlQuery4 = """
                SELECT qty.activity_id, qty.location_id, sum(quantity) as quantity
                FROM SELECT q1
                where a=b
                GROUP by qty.activity_id, qty.location_id
                   """.trimIndent()
                val q4 = Mapping.fromQuery(UnSQLizeSelectUseCase().fromsql("q4", sqlQuery4))
                println(SQLizeSelectUseCase().sqlize(q4))


        }
}