package unibz.cs.semint.kprime.usecase.service

import unibz.cs.semint.kprime.domain.ddl.DatabaseTrademark
import unibz.cs.semint.kprime.domain.dml.ChangeSet


interface SQLizeServiceI {
    fun sqlize(dbTrademark: DatabaseTrademark,change: ChangeSet): List<String>
}