package unibz.cs.semint.kprime.usecase.service

import unibz.cs.semint.kprime.domain.dml.ChangeSet


interface SQLizeServiceI {
    fun sqlize(change: ChangeSet): List<String>
}