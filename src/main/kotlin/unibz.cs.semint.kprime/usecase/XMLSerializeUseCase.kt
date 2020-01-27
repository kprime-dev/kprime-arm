package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.*
import unibz.cs.semint.kprime.domain.ddl.Constraint
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.ddl.Table
import unibz.cs.semint.kprime.domain.dml.ChangeSet
import unibz.cs.semint.kprime.usecase.service.IXMLSerializerService

class XMLSerializeUseCase(val xmlSerializerService: IXMLSerializerService) {


    // table

    fun serializeTable(table: Table): UseCaseResult<String> {
        return UseCaseResult("done",xmlSerializerService.serializeTable(table))
    }

    fun deserializeTable(table: String): UseCaseResult<Table> {
        return UseCaseResult("done",xmlSerializerService.deserializeTable(table))
    }

    fun prettyTable(table: Table): UseCaseResult<String> {
        return UseCaseResult("done",xmlSerializerService.prettyTable(table))
    }

    // database

    fun serializeDatabase(db: Database): UseCaseResult<String> {
        return UseCaseResult("done",xmlSerializerService.serializeDatabase(db))
    }

    fun deserializeDatabase(db: String): UseCaseResult<Database> {
        return UseCaseResult("done",xmlSerializerService.deserializeDatabase(db))
    }

    fun prettyDatabase(db: Database): UseCaseResult<String> {
        return UseCaseResult("done",xmlSerializerService.prettyDatabase(db))
    }

    // constraint

    fun serializeConstraint(constraint: Constraint): UseCaseResult<String> {
        return UseCaseResult("done",xmlSerializerService.serializeConstraint(constraint))
    }

    fun deserializeConstraint(constraint: String): UseCaseResult<Constraint> {
        return UseCaseResult("done",xmlSerializerService.deserializeConstraint(constraint))
    }

    // changeset

    fun serializeChangeSet(changeset: ChangeSet): UseCaseResult<String> {
        return UseCaseResult("done",xmlSerializerService.serializeChangeSet(changeset))
    }

    fun deserializeChangeSet(changeset: String): UseCaseResult<ChangeSet> {
        return UseCaseResult("done",xmlSerializerService.deserializeChangeSet(changeset))
    }

    fun prettyChangeSet(changeset: ChangeSet): UseCaseResult<String> {
        return UseCaseResult("done",xmlSerializerService.prettyChangeSet(changeset))
    }

    // trasnfomer

    fun serializeTransformer(transformer: Transformer): UseCaseResult<String> {
        return UseCaseResult("done",xmlSerializerService.serializeTransfomer(transformer))
    }

    fun deserializeTransformer(transformerXml: String): UseCaseResult<Transformer> {
        return UseCaseResult("done",xmlSerializerService.deserializeTransformer(transformerXml))
    }

}