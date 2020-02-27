package unibz.cs.semint.kprime.domain

import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import java.time.LocalDateTime

data class Transformation(val changeset: ChangeSet, val newdb: Database, val message: String) {
    val timestamp = LocalDateTime.now()
    override fun toString(): String {
        return "Transformation(timestamp=$timestamp, changeset=$changeset, newdb=$newdb, message='$message')"
    }


}