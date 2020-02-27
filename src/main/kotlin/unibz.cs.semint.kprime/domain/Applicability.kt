package unibz.cs.semint.kprime.domain

data class Applicability(val ok: Boolean, val message: String, val tranformerParmeters: MutableMap<String, Any>)