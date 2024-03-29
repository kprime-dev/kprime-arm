package unibz.cs.semint.kprime.usecase.common

import liquibase.database.core.H2Database
import liquibase.sqlgenerator.SqlGenerator
import liquibase.sqlgenerator.SqlGeneratorChain
import liquibase.sqlgenerator.core.AddColumnGenerator
import liquibase.statement.NotNullConstraint
import liquibase.statement.SqlStatement
import liquibase.statement.core.AddColumnStatement
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.ddl.*
import unibz.cs.semint.kprime.domain.dql.*
import java.util.*

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

    fun createViewCommands(changeSet: ChangeSet): List<String> {
        val commands = mutableListOf<String>()
        for (createMapping in changeSet.createMapping)
            commands.add(createViewCommand(createMapping))
        return commands
    }

    fun createViewCommand(mapping: Query): String {
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
            commands.addAll(createConstraintCommand(createConstraint))
        for (createColumn in changeset.createColumn)
            commands.addAll(createColumnCommands(createColumn))
        if (changeset.alterTable!=null) {
            for (alterTable in changeset.alterTable!!)
                commands.add(createAlterTableCommand(alterTable))
        }
        return commands
    }

    fun createTableMappings(changeset: ChangeSet): List<String> {
        val commands = mutableListOf<String>()
        for (createMapping in changeset.createMapping)
            commands.add(createMappingCommand(createMapping))
        for (createConstraint in changeset.createConstraint)
            commands.addAll(createConstraintCommand(createConstraint))
        for (createColumn in changeset.createColumn)
            commands.addAll(createColumnCommands(createColumn))
        if (changeset.alterTable!=null) {
            for (alterTable in changeset.alterTable!!)
                commands.add(createAlterTableCommand(alterTable))
        }
        return commands
    }

    private fun createAlterTableCommand(alterTable: AlterTable): String {
        return alterTable.statement
    }

    class TreeSetSqlGeneratorChain: SqlGeneratorChain<SqlStatement>(TreeSet<SqlGenerator<SqlStatement>>()) {}

    private fun createColumnCommands(createTableColumn: CreateColumn): List<String> {
        val addColumns = createTableColumn.columns.map {
            col -> AddColumnStatement(createTableColumn.catalog,
                                        createTableColumn.schema,
                                        createTableColumn.name,
                                        col.name,
                                        col.dbtype,
                                        col.default,
                                        NotNullConstraint())}
        val addColumnsStatement = AddColumnStatement(addColumns)
        val generateSql = AddColumnGenerator().generateSql(
                addColumnsStatement,
                H2Database(),
                TreeSetSqlGeneratorChain())
        return generateSql.map { gs -> gs.toSql() }
    }

    private fun createConstraintCommand(createConstraint: CreateConstraint):List<String> {
        return when (createConstraint.type) {
            Constraint.TYPE.PRIMARY_KEY.name -> { createPrimaryKey(createConstraint) }
            Constraint.TYPE.FOREIGN_KEY.name -> { createForeignKey(createConstraint) }
            else -> emptyList()
        }
    }

    private fun createForeignKey(createConstraint: CreateConstraint): List<String> {
        val srcTable = createConstraint.source.table
        val trgTable = createConstraint.target.table
        val srcCols = createConstraint.source.columns.joinToString(",")
        val trgCols = createConstraint.target.columns.joinToString(",")
        return listOf("ALTER TABLE $srcTable\n" +
                "ADD FOREIGN KEY ($srcCols) REFERENCES $trgTable($trgCols); ")
    }

    private fun createPrimaryKey(createConstraint: CreateConstraint):List<String> {
        val cols = createConstraint.source.columns.joinToString(",")
        var sql = mutableListOf<String>()
        for (col in createConstraint.source.columns.map { it -> it.name }) {
            sql.add("ALTER TABLE ${createConstraint.source.table} ALTER COLUMN $col Varchar NOT NULL")
        }
        sql.add("ALTER TABLE ${createConstraint.source.table} ADD PRIMARY KEY ($cols)")
        return sql
    }

    private fun createTableCommand(createTable: CreateTable): String {
        var cols = "  "
        for (col in createTable.columns) {
            var colType = col.dbtype
            if (colType == null || colType.trim().isEmpty()) colType = "varchar(64)"
            cols+="${col.name} ${colType} ,"
        }
        cols = cols.dropLast(2)
        return "CREATE TABLE IF NOT EXISTS ${createTable.name} ($cols);"
    }

    private fun createViewCommand(createView: CreateView):String {
        return "CREATE VIEW ${createView.viewName} AS ${createView.text};"
    }

    private fun createMappingCommand(mapping: CreateMapping):String {
        var command = """
CREATE TABLE public.${mapping.name} AS
${SQLizeSelectUseCase().sqlize(mapping)}
                    """.trimIndent()
        return command
    }
}