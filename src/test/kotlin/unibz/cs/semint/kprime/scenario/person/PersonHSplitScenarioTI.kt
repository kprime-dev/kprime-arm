package unibz.cs.semint.kprime.scenario.person

import org.junit.Test
import unibz.cs.semint.kprime.domain.db.Column
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Table
import unibz.cs.semint.kprime.usecase.current.HSplitUseCase

class PersonHSplitScenarioTI {

    private fun buildPersonMetadata(): Database {
        val db = Database()
        val personTable = Table()
        personTable.name= "person"
        val colSSN = Column("SSN", "id.SSN", "dbname.SSN")
        personTable.columns.add(colSSN)
        colSSN.nullable=false
        val colT = Column("T", "id.T", "dbname.T")
        personTable.columns.add(colT)
        colT.nullable=false
        val colS = Column("S", "id.S", "dbname.S")
        colS.nullable=true
        personTable.columns.add(colS)
        db.schema.tables().add(personTable)

        val primaryConstraint = Constraint()
        primaryConstraint.name="primaryKey.person"
        primaryConstraint.source.table="person"
        primaryConstraint.source.columns.add(colSSN)
        primaryConstraint.source.columns.add(colT)
        primaryConstraint.type= Constraint.TYPE.PRIMARY_KEY.name
        db.schema.constraints().add(primaryConstraint)

        return db
    }

    @Test
    fun test_person_hsplit_scenario() {
        // given
        val personHSplitUseCase = HSplitUseCase()
        // when
        personHSplitUseCase.compute(databaseMetadata = buildPersonMetadata())
        // then
        // prints splitted database
    }
}