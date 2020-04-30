package unibz.cs.semint.kprime.usecase.common

import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dql.*

class SQLizeCreateUseCase {

    /**
     * Given a DB will return SQL commands to create VIEWS that DB.
     */
    fun createViewCommands(db: Database): List<String> {
        var sqlCommands = mutableListOf<String>()
        for (mapping in db.mappings()) {
                var command = """
CREATE OR REPLACE VIEW public.${mapping.name} AS
${SQLizeSelectUseCase().sqlize(mapping)}
                """.trimIndent()
                sqlCommands.add(command.trimIndent())
        }
        return sqlCommands
    }


}