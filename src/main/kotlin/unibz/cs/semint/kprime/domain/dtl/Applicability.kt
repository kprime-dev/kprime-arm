package unibz.cs.semint.kprime.domain.dtl

data class Applicability(val ok: Boolean, val message: String, val tranformerParmeters: Map<String, Any>)