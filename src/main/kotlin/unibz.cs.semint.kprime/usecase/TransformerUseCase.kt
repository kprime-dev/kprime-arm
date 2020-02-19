package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.ddl.Database

interface TransformerUseCase {
    fun decompose(db: Database, vararg params:String): Database
    fun compose(db: Database, vararg params:String): Database
}