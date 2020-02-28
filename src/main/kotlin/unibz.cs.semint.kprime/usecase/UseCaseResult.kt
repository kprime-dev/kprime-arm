package unibz.cs.semint.kprime.usecase

data class UseCaseResult<T>(val message : String, val ok : T?, val ko : Any = Unit) {}