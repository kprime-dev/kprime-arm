package unibz.cs.semint.kprime.adapter.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import unibz.cs.semint.kprime.domain.Transformer
import unibz.cs.semint.kprime.domain.db.Constraint
import unibz.cs.semint.kprime.domain.db.Database
import unibz.cs.semint.kprime.domain.db.Table
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.usecase.service.SerializerServiceI
import java.io.StringWriter

class YAMLSerializerJacksonAdapter : SerializerServiceI {
    override fun serializeTable(table: Table): String {
        TODO("Not yet implemented")
    }

    override fun deserializeTable(s: String): Table {
        TODO("Not yet implemented")
    }

    override fun prettyTable(table: Table): String {
        TODO("Not yet implemented")
    }

    override fun serializeDatabase(database: Database): String {
        TODO("Not yet implemented")
    }

    override fun deserializeDatabase(s: String): Database {
        TODO("Not yet implemented")
    }

    override fun prettyDatabase(db: Database): String {
        val objectMapper = ObjectMapper(YAMLFactory())
        val outWriter = StringWriter()
        objectMapper.writeValue(outWriter,db)
        return outWriter.toString()
    }

    override fun prettyJsonDatabase(db: Database): String {
        TODO("Not yet implemented")
    }

    override fun serializeConstraint(constraint: Constraint): String {
        TODO("Not yet implemented")
    }

    override fun deserializeConstraint(s: String): Constraint {
        TODO("Not yet implemented")
    }

    override fun serializeChangeSet(changeset: ChangeSet): String {
        TODO("Not yet implemented")
    }

    override fun deserializeChangeSet(changeset: String): ChangeSet {
        TODO("Not yet implemented")
    }

    override fun prettyChangeSet(table: ChangeSet): String {
        TODO("Not yet implemented")
    }

    override fun serializeTransfomer(transformer: Transformer): String {
        TODO("Not yet implemented")
    }

    override fun deserializeTransformer(transformer: String): Transformer {
        TODO("Not yet implemented")
    }

    override fun deepclone(database: Database): Database {
        TODO("Not yet implemented")
    }
}