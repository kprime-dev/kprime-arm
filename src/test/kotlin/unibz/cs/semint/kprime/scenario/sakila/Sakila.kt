package unibz.cs.semint.kprime.scenario.sakila

import unibz.cs.semint.kprime.adapter.repository.MetaSchemaJdbcAdapter
import unibz.cs.semint.kprime.adapter.service.XMLSerializerJacksonAdapter
import unibz.cs.semint.kprime.domain.datasource.DataSource
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.usecase.common.MetaSchemaReadUseCase

fun readMeta(sakilaSource: DataSource): Database? {
    val result = MetaSchemaReadUseCase().doit(sakilaSource,
            "read-meta-schema sakila-source",
            MetaSchemaJdbcAdapter(),
            XMLSerializerJacksonAdapter())
    return result.ok
}

fun sakilaDataSource(): DataSource {
    val user = System.getenv()["sakila_user"] ?: ""//"npedot"
    val pass = System.getenv()["sakila_pass"] ?: ""//"password"
    val sakilaSource = DataSource(
            "psql",
            "sakila-source",
            "org.postgresql.Driver",
            "jdbc:postgresql://localhost:5432/sakila", user, pass)
    return sakilaSource
}
