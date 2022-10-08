package unibz.cs.semint.kprime.usecase.repository

import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.db.Database

interface IMetaSchemaRepository {
    fun metaDatabase(datasource: DataSource) : Database
}