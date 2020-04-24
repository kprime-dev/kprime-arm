package unibz.cs.semint.kprime.support

val <T> T.exhaustive: T
    get() = this

sealed class Result<out T> {
    data class Success<T>(val value: T) : Result<Nothing>()
    data class Error(val message: String, val cause: Exception? = null) : Result<Nothing>()
}

data class IBAN(val prefix:String)

fun compute(name:String) : Result<IBAN> {
    if (name.startsWith("IT"))
    return Result.Success<IBAN>(IBAN("ITaaa"))
    else
        return Result.Error("No IT")
}

fun check(input:String) {
    when(val result = compute(input)) {
        is Result.Success<*> -> println((result.value as IBAN).prefix)
        is Result.Error -> println(result.message)
    }.exhaustive
}

fun main() {
    check("aaaa")
}