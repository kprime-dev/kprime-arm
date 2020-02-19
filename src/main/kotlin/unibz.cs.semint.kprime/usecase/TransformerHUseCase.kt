package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.ddl.Database

class TransformerHUseCase :TransformerUseCase {
    override fun decompose(db: Database, vararg params:String): Database {
        // TODO("not implemented")
        return Database()
    }

    override fun compose(db: Database, vararg params:String): Database {
        // TODO("not implemented")
        return Database()
    }

}