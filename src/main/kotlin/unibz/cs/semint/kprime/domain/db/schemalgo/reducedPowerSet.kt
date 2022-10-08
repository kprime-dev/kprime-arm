package unibz.cs.semint.kprime.domain.db.schemalgo

fun <T> reducedPowerSet(originalSet: Set<T>): Set<Set<T>> {
    var result = powerSet(originalSet)
    result = result.minus(HashSet<T>()) as Set<Set<T>>
    return result
}
