package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.ChangeSet
import unibz.cs.semint.kprime.domain.Database

interface TransformerUseCase {
    fun compute(db: Database):ChangeSet
}