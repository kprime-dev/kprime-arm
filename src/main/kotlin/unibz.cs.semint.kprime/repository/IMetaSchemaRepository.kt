package unibz.cs.semint.kprime.repository

import unibz.cs.semint.kprime.domain.DataSource
import unibz.cs.semint.kprime.domain.Database

interface IMetaSchemaRepository {
    fun metaDatabase(datasource: DataSource) : Database
}