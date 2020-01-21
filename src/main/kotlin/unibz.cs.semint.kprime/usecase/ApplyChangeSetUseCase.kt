package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.ChangeSet
import unibz.cs.semint.kprime.domain.Database

/**
 * Given a Databse will apply changes following a given changeSet modification.
 */
class ApplyChangeSetUseCase {
    fun apply(db: Database, changeset:ChangeSet):Database {
        // TODO for every create* and remove* will do it on a cloned db
        return Database()
    }
}