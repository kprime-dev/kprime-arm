package unibz.cs.semint.kprime.domain.ddl

data class Relation(val table:Table, val constraints: Set<Constraint>)