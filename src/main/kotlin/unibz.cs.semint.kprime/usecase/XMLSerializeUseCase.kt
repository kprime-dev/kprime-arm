package unibz.cs.semint.kprime.usecase

import unibz.cs.semint.kprime.domain.*
import unibz.cs.semint.kprime.service.IXMLSerializerService

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
}