package unibz.cs.semint.kprime.usecase.common

import unibz.cs.semint.kprime.domain.ddl.Constraint
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.domain.dml.CreateConstraint
import unibz.cs.semint.kprime.domain.dml.CreateTable
import unibz.cs.semint.kprime.domain.dml.CreateView
import unibz.cs.semint.kprime.domain.dql.*

class SQLizeCreateUseCase {

    /**
     * Given a DB will return SQL commands to create VIEWS that DB.
     */
    fun createViewCommands(db: Database): List<String> {
        var sqlCommands = mutableListOf<String>()
        for (mapping in db.mappings()) {
            var command = createViewCommand(mapping)
            sqlCommands.add(command.trimIndent())
        }
        return sqlCommands
    }

    private fun createViewCommand(mapping: Query): String {
        var command = """
    CREATE OR REPLACE VIEW public.${mapping.name} AS
    ${SQLizeSelectUseCase().sqlize(mapping)}
                    """.trimIndent()
        return command
    }

    fun createCommands(changeset: ChangeSet): List<String> {
        val commands = mutableListOf<String>()
        for (createTable in changeset.createTable)
            commands.add(createTableCommand(createTable))
        for (createView in changeset.createView)
            commands.add(createViewCommand(createView))
        for (createMapping in changeset.createMapping)
            commands.add(createViewCommand(createMapping))
        for (createConstraint in changeset.createConstraint)
            commands.add(createConstraintCommand(createConstraint))
        return commands
    }

    private fun createConstraintCommand(createConstraint: CreateConstraint):String {
        when (createConstraint.type) {
            Constraint.TYPE.PRIMARY_KEY.name -> { return createPrimaryKey(createConstraint)}
            Constraint.TYPE.FOREIGN_KEY.name -> { return createForeignKey(createConstraint)}
            else -> return ""
        }

    }

    private fun createForeignKey(createConstraint: CreateConstraint): String {
        val srcTable = createConstraint.source.table
        val trgTable = createConstraint.target.table
        val srcCols = createConstraint.source.columns.joinToString(",")
        val trgCols = createConstraint.target.columns.joinToString(",")
        return "ALTER TABLE $srcTable\n" +
                "ADD FOREIGN KEY ($srcCols) REFERENCES $trgTable($trgCols); "
    }

    private fun createPrimaryKey(createConstraint: CreateConstraint):String {
        val cols = createConstraint.source.columns.joinToString(",")
        return "ALTER TABLE ${createConstraint.source.table} ADD PRIMARY KEY ($cols)"
    }

    private fun createTableCommand(createTable: CreateTable): String {
        var cols = "  "
        for (col in createTable.columns) {
            cols+="${col.name} ${col.dbtype} ,"
        }
        cols = cols.dropLast(2)
        return "CREATE TABLE ${createTable.name} ($cols);"
    }

    private fun createViewCommand(createView: CreateView):String {
        return "CREATE VIEW ${createView.viewName} AS ${createView.text};"
    }
}