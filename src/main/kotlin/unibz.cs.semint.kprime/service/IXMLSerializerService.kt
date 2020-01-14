package unibz.cs.semint.kprime.service

import unibz.cs.semint.kprime.domain.Constraint
import unibz.cs.semint.kprime.domain.Database
import unibz.cs.semint.kprime.domain.Table

interface IXMLSerializerService {
    fun serializeTable(table: Table): String
    fun deserializeTable(s: String): Table
    fun prettyTable(table: Table): String
    fun serializeDatabase(database: Database): String
    fun deserializeDatabase(s: String): Database
    fun prettyDatabase(db: Database): String
    fun serializeConstraint(constraint: Constraint): String
    fun deserializeConstraint(s: String): Constraint
}