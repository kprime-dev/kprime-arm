package unibz.cs.semint.kprime.usecase.repository

import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.ddl.Database

interface IMetaSchemaRepository {
    fun metaDatabase(datasource: DataSource) : Database
}