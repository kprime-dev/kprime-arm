package unibz.cs.semint.kprime.domain

data class UseCaseResult<T>(val message : String, val ok : T?, val ko : Any = Unit) {}