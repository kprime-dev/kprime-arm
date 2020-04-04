package unibz.cs.semint.kprime.usecase.service

import unibz.cs.semint.kprime.domain.*
import unibz.cs.semint.kprime.domain.ddl.Constraint
import unibz.cs.semint.kprime.domain.ddl.Database
import unibz.cs.semint.kprime.domain.ddl.Table
import unibz.cs.semint.kprime.domain.dml.ChangeSet

interface SerializerServiceI {
    fun serializeTable(table: Table): String
    fun deserializeTable(s: String): Table
    fun prettyTable(table: Table): String
    fun serializeDatabase(database: Database): String
    fun deserializeDatabase(s: String): Database
    fun prettyDatabase(db: Database): String
    fun serializeConstraint(constraint: Constraint): String
    fun deserializeConstraint(s: String): Constraint
    fun serializeChangeSet(changeset: ChangeSet): String
    fun deserializeChangeSet(changeset: String): ChangeSet
    fun prettyChangeSet(table: ChangeSet): String
    fun serializeTransfomer(transformer: Transformer): String
    fun deserializeTransformer(transformer: String): Transformer
    fun deepclone(database: Database): Database
}