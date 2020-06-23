package unibz.cs.semint.kprime.usecase.common

import unibz.cs.semint.kprime.domain.ddl.Constraint
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.domain.dml.DropConstraint
import unibz.cs.semint.kprime.domain.dml.DropTable
import unibz.cs.semint.kprime.domain.dml.DropView

class SQLizeDropUseCase {

    fun dropCommands(changeset: ChangeSet): List<String> {
        val commands = mutableListOf<String>()
        for (dropTable in changeset.dropTable)
            commands.add(dropTableCommand(dropTable))
        for (dropView in changeset.dropView)
            commands.add(dropViewCommand(dropView))
        for (dropConstraint in changeset.dropConstraint)
            commands.add(dropConstraintCommand(dropConstraint))
        return commands
    }

    private fun dropConstraintCommand(dropConstraint: DropConstraint): String {
        when(dropConstraint.type) {
            Constraint.TYPE.PRIMARY_KEY.name -> return dropPrimaryKeyCommand(dropConstraint)
            Constraint.TYPE.FOREIGN_KEY.name -> return dropForeignKeyCommand(dropConstraint)
        }
        return ""
    }

    private fun dropForeignKeyCommand(dropConstraint: DropConstraint): String {
        return "ALTER TABLE ${dropConstraint.tableName} DROP FOREIGN KEY ${dropConstraint.constraintName};"
    }

    private fun dropPrimaryKeyCommand(dropConstraint: DropConstraint):String {
        return "ALTER TABLE ${dropConstraint.tableName} DROP PRIMARY KEY;"
    }

    private fun dropViewCommand(dropView: DropView): String {
        return "DROP VIEW ${dropView.viewName};"
    }

    private fun dropTableCommand(dropTable: DropTable): String {
        return "DROP TABLE ${dropTable.tableName};"
    }


}