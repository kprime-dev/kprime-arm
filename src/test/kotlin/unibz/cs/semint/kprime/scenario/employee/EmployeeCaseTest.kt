package unibz.cs.semint.kprime.scenario.employee

import org.junit.Ignore
import org.junit.Test
import unibz.cs.semint.kprime.adapter.repository.JdbcAdapter
import unibz.cs.semint.kprime.adapter.repository.JdbcPrinter
import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.DataSourceConnection
import unibz.cs.semint.kprime.domain.ddl.Database
import kotlin.test.assertEquals

class EmployeeCaseTest {


    fun employeeDBMonotable():Database {
        val db = Database()
        db.schema.addTable("Employee:SSN,Phone,Name,Department,DAddress")
//        db.schema.addMultivalued("Employee:SSN->>Phone")
//        db.schema.addFunctional("Employee:SSN-->Name")
//        db.schema.addFunctional("Employee:SSN-->Departmente")
//        db.schema.addFunctional("Employee:SSN-->DAddress")
//        db.schema.addDoubleInc("Employee:Department<->DAddress")
        //db.schema.constraints()

        // given
        val dataSourceConnection = DataSourceConnection("test","sa","",true,true,false)
        val dataSource = DataSource("h2", "testdb", "org.h2.Driver", "jdbc:h2:mem:test_mem", "sa", "")
        dataSource.connection = dataSourceConnection
        val sqlAdapter = JdbcAdapter()
        // when
        sqlAdapter.create(dataSource,"CREATE TABLE Employee(SSN varchar(64),Phone varchar(64),Name varchar(64),Department varchar(64),DAddress varchar(64))")
        sqlAdapter.create(dataSource, "INSERT INTO Employee VALUES('SSN1','Phone1','Name1','Department1','DAddress1')")
        sqlAdapter.create(dataSource, "INSERT INTO Employee VALUES('SSN2','Phone2','Name2','Department2','DAddress2')")
        sqlAdapter.create(dataSource, "INSERT INTO Employee VALUES('SSN3','Phone3','Name3',NULL,NULL)")

        sqlAdapter.create(dataSource,"CREATE VIEW V1_SSN_Phone AS SELECT SSN,Phone FROM Employee")
        sqlAdapter.create(dataSource,"CREATE VIEW V1_SSN_Other AS SELECT SSN,Name,Department,DAddress FROM Employee")

        sqlAdapter.create(dataSource,"CREATE VIEW V2_SSN_Name AS SELECT SSN,Name FROM V1_SSN_Other")
        sqlAdapter.create(dataSource,"CREATE VIEW V2_SSN_Other AS SELECT SSN,Department,DAddress FROM V1_SSN_Other")

        // We don't require to test nullability of DAddress cause there is a double inc constraint with Departement.
        sqlAdapter.create(dataSource,"CREATE VIEW V3_SSN_Other_NO_NULL AS SELECT * FROM V1_SSN_Other WHERE Department IS NOT NULL")
        sqlAdapter.create(dataSource,"CREATE VIEW V3_SSN_Other_WITH_NULL AS SELECT SSN,Name FROM V1_SSN_Other WHERE Department IS NULL")

        sqlAdapter.create(dataSource,"CREATE VIEW Employee2 AS SELECT * FROM V3_SSN_Other_WITH_NULL as TA CROSS JOIN V3_SSN_Other_NO_NULL as TB")



        // then
        sqlAdapter.query (dataSource, "SELECT * FROM V3_SSN_Other_WITH_NULL", JdbcPrinter::printJsonResultSet)
        sqlAdapter.query (dataSource, "SELECT * FROM V3_SSN_Other_NO_NULL", JdbcPrinter::printJsonResultSet)
        dataSourceConnection.closed = true
        sqlAdapter.query (dataSource, "SELECT * FROM Employee2", JdbcPrinter::printJsonResultSet)

        sqlAdapter

        return db
    }

    @Test
    @Ignore
    fun test_employee_to_arm_1_2(){
        TODO()
        // given
        val employeeDb = employeeDBMonotable();
        // assertEquals(1, employeeDb.schema.tables?.size)
        // when
        val dbNew = verticalDecomposeMVD(employeeDb,"SSN->>Phone")
        // then
        //assertEquals(2,dbNew.schema.tables?.size)
    }

    private fun verticalDecomposeMVD(db: Database, s: String): Database {

        return Database()
    }
}