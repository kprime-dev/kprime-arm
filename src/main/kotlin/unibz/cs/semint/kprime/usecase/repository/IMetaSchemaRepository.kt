package unibz.cs.semint.kprime.usecase.repository

import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.db.Database

interface IMetaSchemaRepository {
    //fun metaDatabase(datasource: DataSource, db:Database, tableName : String = "") : Database
    fun metaDatabase(
        datasource: DataSource,
        db: Database,
        tableName: String,
        catalog: String?,
        schema: String?
    ): Database
}