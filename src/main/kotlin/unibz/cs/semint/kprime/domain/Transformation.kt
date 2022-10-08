package unibz.cs.semint.kprime.domain

import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.ddl.ChangeSet
import java.time.LocalDateTime

data class Transformation(val changeset: ChangeSet, val newdb: Database, val message: String) {
    val timestamp = LocalDateTime.now()
    override fun toString(): String {
        var flag = ""
        if (changeset.size()==0) flag = "EMPTY"
        return "Transformation(timestamp=$timestamp, $flag changeset=$changeset, newdb=$newdb, message='$message')"
    }


}