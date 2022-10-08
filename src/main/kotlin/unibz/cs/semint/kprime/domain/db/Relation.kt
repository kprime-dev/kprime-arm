package unibz.cs.semint.kprime.domain.db

data class Relation(val table:Table, val constraints: Set<Constraint>)