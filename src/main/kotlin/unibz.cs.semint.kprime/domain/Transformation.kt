package unibz.cs.semint.kprime.domain

import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import java.time.LocalDateTime

data class Transformation(val changeset: ChangeSet, val newdb: Database, val message: String) {
    val timestamp = LocalDateTime.now()
    override fun toString(): String {
        var flag = ""
        if (changeset==null || changeset.size()==0) flag = "EMPTY"
        return "Transformation(timestamp=$timestamp, $flag changeset=$changeset, newdb=$newdb, message='$message')"
    }


}