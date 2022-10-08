package unibz.cs.semint.kprime.usecase.service

import unibz.cs.semint.kprime.domain.db.DatabaseTrademark
import unibz.cs.semint.kprime.domain.ddl.ChangeSet


interface SQLizeServiceI {
    fun sqlize(dbTrademark: DatabaseTrademark,change: ChangeSet): List<String>
}