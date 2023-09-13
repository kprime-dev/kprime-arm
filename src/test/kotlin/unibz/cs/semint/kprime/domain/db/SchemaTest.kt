package unibz.cs.semint.kprime.domain.db

import junit.framework.TestCase.*
import org.junit.Test
import unibz.cs.semint.kprime.domain.db.schemalgo.*

class SchemaTest {


    @Test
    fun test_basic() {
        val time = Column.of("Time")
        val classroom = Column.of("Classroom")
        val course = Column.of("Course")
        val fd = Constraint.of("${time.name},${classroom.name}", course.name)

        assertEquals(" Time , Classroom , Course"," $time , $classroom , $course")
        assertEquals("  :Time,Classroom --> :Course ; "," $fd")
        assertEquals(" :Time,Classroom --> :Course ; ",fd.toString())
    }

    @Test
    fun test_find_superkeys() {
        val exprs = listOf(
                "A, B --> C",
                "C, D --> E",
                "C --> A",
                "C --> D",
                "D-->B"
        )

        val expr = "A, B --> C; C, D --> E; C --> A; C --> D; D-->B"
        val fds = Constraint.set(exprs)
        val attrs = Column.set("A, B, C, D, E")
        val keys = superkeys(attrs,fds)
        assertEquals(
                "[[A, B], [C], [A, C], [A, D], [B, C], [A, B, C], [A, B, D], [C, D], [A, B, E], [A, C, D], [C, E], [A, C, E], [B, C, D], [A, B, C, D], [A, D, E], [B, C, E], [A, B, C, E], [A, B, D, E], [C, D, E], [A, C, D, E], [B, C, D, E], [A, B, C, D, E]]"
                ,keys.toString()
                )
    }

    @Test
    fun test_find_keys() {
        val constraints = Constraint.set("A, B --> C; C, D --> E; C --> A; C --> D; D --> B")
        val columns = Column.set("A, B, C, D, E")
        val keys = keys(columns, constraints)
        assertEquals(
                "[[A, B], [C], [A, D]]",
                keys.toString()
        )
    }

    @Test
    fun test_closure() {
        val columns = Column.set("C,S")
        val constraints = Constraint.set("C-->T;H,R-->C;H,T-->R;C,S-->G;H,S-->R")

        val closure = closure(columns, constraints)
        assertEquals("[C, S, T, G]",closure.toString())
    }

    @Test
    fun test_remove_trivials() {
        val constraints = Constraint.set("A-->B;"
                + "A,B-->B;"
                + "A,B-->A;"
                + "C-->C;"
                + "C,D,E,F-->C,D,F")
        val result= removeTrivial(constraints)
        assertEquals("[ :A --> :B ; ]",result.toString())
    }

    @Test
    fun test_equivalent() {
        val setA = Constraint.set("A-->C; A,C-->D; E-->A,D; E-->H")
        val setB = Constraint.set("A-->C,D; E-->A,H")
        assertTrue(equivalent(setA,setB))
    }

    @Test
    fun test_powerset() {
        // given
        val attrs = Column.set("A,B,C")
        val notin = Column.set("D,E")
        val fds = Constraint.set("A-->B,C;C,D-->E;E-->A;B-->D")
        // when
        val powerSet = powerSet(attrs)
        // then
        val map = HashMap<Set<Column>,Set<Column>>()
        for (sa in powerSet) {
            map.put(sa,closure(sa,fds))
        }
        var result = ""
        for (k in map.keys) {
            var v = map.get(k)
            if (v!=null) {
                v = v.minus(notin)
                result+="$k = $v "+System.lineSeparator()
            }
        }
        assertEquals("""
             [] = [] 
             [A] = [A, B, C] 
             [B] = [B] 
             [A, B] = [A, B, C] 
             [C] = [C] 
             [A, C] = [A, B, C] 
             [B, C] = [A, B, C] 
             [A, B, C] = [A, B, C]
        """.trimIndent(),result.trim())
    }

    @Test
    fun test_removeUnnecessaryEntireFD() {
        var fds = Constraint.set("A-->B,C;B-->C;A-->B;A,B-->C")
        fds = splitRight(fds)
        val removed = removeUnnecessaryEntireFD(fds)
        assertEquals("[ :A --> :B ; ,  :B --> :C ; ]",removed.toString())
    }

    @Test
    fun test_projection() {
        // given
        val attrs = Column.set("name, location, favAppl, appl")
        val fds = Constraint.set("name-->location,favAppl; appl-->provider")
        // when
        val result : Set<Constraint> = projection(attrs,fds)
        // then
        assertEquals(2,result.size)
        assertTrue(result.contains(Constraint.of("name --> favAppl")))
        assertTrue(result.contains(Constraint.of("name --> location")))
    }

    @Test
    fun test_minimalbasis() {
        // given
        val fds = Constraint.set("name --> location;name --> favAppl;appl, name --> favAppl")
        // when
        val basis = minimalBasis(fds)
        // then
        assertEquals(2,basis.size)
        assertTrue(basis.contains(Constraint.of("name --> favAppl")))
        assertTrue(basis.contains(Constraint.of("name --> location")))
}

    @Test
    fun test_combineRight() {
        // given
        var fds = Constraint.set("A-->B;"
                + "A,B-->B,C;"
                + "A-->C;"
                + "B,C-->D;"
                + "B,C-->C,E")
        // when
        fds = combineRight(fds)
        fds = removeTrivial(fds)
        // then
        assertEquals(3,fds.size)
        assertEquals("[ :B,C --> :E,D ; ,  :A --> :C,B ; ,  :A,B --> :C ; ]",fds.toString().trim())
        assertTrue(fds.contains(Constraint.of("A,B --> C")))
        assertTrue(fds.contains(Constraint.of("B,C --> E,D")))
        assertTrue(fds.contains(Constraint.of("A --> C,B")))
    }

    @Test
    fun test_addFunctional() {
        // given
        var schema = Schema()
        assertEquals(0,schema.functionals().size)
        // when
        schema.addFunctional("person:dep_name-->dep_address")
        // then
        assertEquals(1,schema.functionals().size)
    }

    @Test
    fun test_addTable() {
        // given
        var schema = Schema()
        assertEquals(0,schema.tables().size)
        // when
        schema.addTable("person:name,dep_name,dep_address")
        // then
        assertEquals(1,schema.tables().size)
    }

    @Test
    fun test_addKey() {
        // given
        var schema = Schema()
        assertEquals(0,schema.keys().size)
        schema.addTable("person:name,surname")
        // when
        schema.addKey("person:name,surname")
        // then
        assertEquals(1,schema.keys().size)
    }

    @Test
    fun test_addForeignKey() {
        // given
        var schema = Schema()
        assertEquals(0,schema.foreignKeys().size)
        // when
        schema.addForeignKey("person:dep_id-->department:dep_id")
        // then
        assertEquals(1,schema.foreignKeys().size)
    }

    @Test
    fun test_addDoubleInc() {
        // given
        var schema = Schema()
        assertEquals(0,schema.doubleIncs().size)
        // when
        schema.addDoubleInc("table4:DepName<->table4:DepAddress")
        // then
        assertEquals(1,schema.doubleIncs().size)
    }

    @Test
    fun test_addNotNull() {
        // given
        var schema = Schema()
        assertEquals(0,schema.notNull("department").size)
        // when
        schema.addNotNull("department:dep_name,dep_address")
        val notnulls = schema.notNull("department")
        // then
        assertEquals(1,notnulls.size)
        assertEquals(2,notnulls[0].source.columns.size)
        assertEquals("dep_address",notnulls[0].source.columns[0].name)
        assertEquals("dep_name",notnulls[0].source.columns[1].name)
    }

    @Test
    fun test_addUnique() {
        // given
        var schema = Schema()
        assertEquals(0,schema.unique("department").size)
        // when
        schema.addUnique("department:dep_name,dep_address")
        val uniques = schema.unique("department")
        // then
        assertEquals(1,uniques.size)
        assertEquals(2,uniques[0].source.columns.size)
        assertEquals("dep_address",uniques[0].source.columns[0].name)
        assertEquals("dep_name",uniques[0].source.columns[1].name)
    }

    @Test
    fun test_dropTable() {
        // given
        var schema = Schema()
        schema.addTable("person:name,surname")
        assertEquals(1,schema.tables().size)
        // when
        schema.dropTable("person")
        // then
        assertEquals(0, schema.tables().size)

    }

    @Test
    fun test_dropConstraint() {
        // given
        var schema = Schema()
        schema.addTable("person:id")
        schema.addKey("person:id")
        schema.addForeignKey("person:id-->employee:id")
        assertEquals(2,schema.constraints().size)
        // when
        schema.dropConstraint("KEY_person_id")
        schema.dropConstraint("person_employee.foreignKey1")
        // then
        assertEquals(0,schema.constraints().size)
    }

    @Test
    fun test_check3NF() {
        // given
        val attrs = Column.set("A, B, C")
        val fds = Constraint.set("A,B-->C; C-->B")
        // when
        val check3NF = check3NF(attrs, fds)
        // then
        assertTrue(check3NF.isEmpty())
    }

    @Test
    fun test_not_check3NF() {
        // given
        val attrs = Column.set("A, B, C, D")
        val fds = Constraint.set("A,B-->C; C-->D")
        // when
        val check3NF = check3NF(attrs, fds)
        // then
        assertEquals("[ :C --> :D ; ]",check3NF.toString())
        assertEquals(1, check3NF.size)
    }

    @Test
    fun test_checkBNFC() {
        // given
        val attrs = Column.set("name, location, favAppl, application, provider")
        val fds = Constraint.set("name-->location; name-->favAppl; application-->provider")
        // when
        val result = checkBCNF(attrs,fds)
        // then
        assertTrue(result.contains(Constraint.of("name --> favAppl")))
        assertTrue(result.contains(Constraint.of("name --> location")))
        assertTrue(result.contains(Constraint.of("application --> provider")))
        assertEquals(3,result.size)
    }

    @Test
    fun test_checkLossyDecomposition() {
        // given
        val attrs = Column.set("A,B,C,D,E")
        val subattrs1 = Column.set("A,B,C")
        val subattrs2 = Column.set("A,D,E")
        val subattrs = setOf(subattrs1,subattrs2)
        val fds = Constraint.set("A-->B,C;C,D-->E;E-->A;B-->D")
        // when
        val result = checkLossyDecomposition(attrs, fds, subattrs)
        // then
        assertTrue(result.contains(Constraint.of("C , D --> E")))
        assertTrue(result.contains(Constraint.of("B --> D")))
        assertEquals(2,result.size)

    }

    @Test
    fun test_deomposeTo3NF() {
        // given
        val attrs = Column.set("C, T, H, R, S, G")
        val fds = Constraint.set("C-->T;H,R-->C;H,T-->R;C,S-->G;H,S-->R")
        assertEquals(5,fds.size)
        // when
        val result = decomposeTo3NF(attrs, fds)
        // then
        val resultTables = HashSet<List<Column>>()
        val resultConstraints = HashSet<Set<Constraint>>()
        for (relation in result) {
//            println("columns")
//            println(relation.table.columns)
            resultTables.add(relation.table.columns)
//            println("constr")
//            println(relation.constraints)
            resultConstraints.add(relation.constraints)
//            println()
        }
        assertTrue(resultConstraints.contains(Constraint.set("R , H --> C ; C , H --> R")))
        assertTrue(resultConstraints.contains(Constraint.set("C , S --> G")))
        assertTrue(resultConstraints.contains(Constraint.set("S , H --> R")))
        assertTrue(resultConstraints.contains(Constraint.set("R , H --> C ; C , H --> R")))
        assertTrue(resultConstraints.contains(Constraint.set("T , H --> R ; R , H --> T")))
        assertEquals(5, resultConstraints.size)

        assertTrue(checkContains(resultTables, Column.set("H, T, R")))
        assertTrue(checkContains(resultTables, Column.set("T, C")))
        assertTrue(checkContains(resultTables, Column.set("C, S, G")))
        assertTrue(checkContains(resultTables, Column.set("H, S, R")))
        assertTrue(checkContains(resultTables, Column.set("H, R, C")))
        assertEquals(5, resultTables.size)
    }

    private fun checkContains(resultTables: HashSet<List<Column>>, element: Set<Column>): Boolean {
        var found = false
        for (tab in resultTables) {
            if (tab.toSet().equals(element)) {
                found = true
                break
            }
        }
        return found
    }

    @Test
    fun test_decomposeToBCNF() {
        // given
        val attrs = Column.set("name, location, favAppl, application, provider")
        val fds = Constraint.set("name-->location; name-->favAppl; application-->provider")
        // when
        val result = decomposeToBCNF(attrs, fds)
        // then
        var resultTables = HashSet<List<Column>>()
        var resultConstraints = HashSet<Set<Constraint>>()
        for (relation in result) {
//            println("columns")
//            println(relation.table.columns)
            resultTables.add(relation.table.columns)
//            println("constr")
//            println(relation.constraints)
            resultConstraints.add(relation.constraints)
//            println()
        }
        assertTrue(resultConstraints.contains(Constraint.set("application --> provider")))
        assertTrue(resultConstraints.contains(Constraint.set("name --> favAppl ; name --> location")))
        assertTrue(resultConstraints.contains(Constraint.set("")))
        assertEquals(3, resultConstraints.size)


        assertTrue(checkContains(resultTables, Column.set("application, provider")))
        assertTrue(checkContains(resultTables, Column.set("application, name")))
        assertTrue(checkContains(resultTables, Column.set("name, favAppl, location")))
        assertEquals(3, resultTables.size)

    }

    @Test
    fun test_decomposeBCNF_failed() {
        val attrs = Column.set("A, B, C")
        val fds = Constraint.set("A,B-->C; C-->B")
        val result = decomposeToBCNF(attrs,fds)
        //
        val violations = checkBCNF(attrs, fds)
        assertFalse(violations.isEmpty())
        for (constraint in violations) {
//            println(constraint.toString())
        }

        var resultConstraints = HashSet<Set<Constraint>>()
        for (relation in result) {
//            println("columns")
//            println(relation.table.columns)
            //resultTables.add(relation.table.columns)
//            println("constr")
//            println(relation.constraints)
            resultConstraints.add(relation.constraints)
//            println()
        }

//        for (constraint in fds) {
//            if (!resultConstraints.contains((setOf(constraint)))) {
//                println ( "Lost "+constraint.toString())
//            }
//        }

    }

    @Test
    fun test_lostBCNFConstraints() {
        // given
        val attrs = Column.set("A, B, C")
        val fds = Constraint.set("A,B-->C; C-->B")
        // when
        val lostBCNFConstraints = lostBCNFConstraints(attrs, fds)
        // then
        assertEquals(1,lostBCNFConstraints.size)
        assertTrue(lostBCNFConstraints.equals(Constraint.set("A,B --> C")))
    }

    @Test
    fun test_not_key_col_finder() {
        // given
        val schema = Schema()
        schema.addTable("person:name,surname,age,address")
        schema.addKey("person:name,surname")
        // when
        val notkey = schema.notkey("person")
        // then
        assertEquals(2,notkey.size)
        assertTrue(notkey.contains(Column.of("age")))
        assertTrue(notkey.contains(Column.of("address")))
    }

    @Test
    fun test_move_constraints_from_table_to_table() {
        // given
        val schema = Schema()
        schema.addTable("person:name")
        schema.addTable("employee:name")
        schema.addDoubleInc("person:name<->employee:name")
        schema.addTable("employee_1:name")
        assertEquals(1,schema.constraintsByTable("employee").size)
        // when
        schema.moveConstraintsFromTableToTable("employee","employee_1")
        // then
        assertEquals(0,schema.constraintsByTable("employee").size)
        assertEquals(1,schema.constraintsByTable("employee_1").size)
        assertEquals("[DOUBLE_INCLUSION person:name --> employee_1:name ; ]",schema.constraintsByTable("employee_1").toString())
    }

    @Test
    fun test_move_constraint_from_cols_to_col() {
        // given
        val schema = Schema()
        schema.addTable("Person:name,surname")
        schema.addKey("Person:name")
        // when
        schema.moveConstraintsFromColsToCol("Person","name","surname")
        //then
        val key = schema.keyCols("Person")
        assertEquals("surname",key.first().name)
        assertEquals(1,key.size)
    }

    @Test
    fun test_is_binary_relation() {
        // given
        val schema = Schema()
        schema.addTable("Person:name,surname")
        schema.addForeignKey("X:a-->Person:name")
        schema.addForeignKey("Y:b-->Person:surname")
        // then
        assertTrue(schema.isBinaryRelation("Person"))
    }
}
