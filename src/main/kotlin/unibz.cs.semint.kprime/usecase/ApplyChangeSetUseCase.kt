package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.ChangeSet
import unibz.cs.semint.kprime.domain.Database
import unibz.cs.semint.kprime.usecase.service.IXMLSerializerService

/**
 * Given a Databse will apply changes following a given changeSet modification.
 */
class ApplyChangeSetUseCase(serializer : IXMLSerializerService) {
    val serializer = serializer
    fun apply(db: Database, changeset:ChangeSet):Database {
        val newdb = serializer.deepclone(db)
        return newdb
    }
}