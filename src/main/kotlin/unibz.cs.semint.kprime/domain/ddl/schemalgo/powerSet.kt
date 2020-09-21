package unibz.cs.semint.kprime.domain.ddl.schemalgo

fun <T> powerSet(originalSet: Set<T>): Set<Set<T>> {
    val sets = HashSet<Set<T>>()
    if (originalSet.isEmpty()) {
        sets.add(HashSet<T>())
        return  sets
    }
    val list = ArrayList<T>(originalSet)
    val head = list.get(0)
    val rest = HashSet<T>(list.subList(1,list.size))
    for (set in powerSet(rest)) {
        val newSet = HashSet<T>()
        newSet.add(head)
        newSet.addAll(set)
        sets.add(newSet)
        sets.add(set)
    }
    return sets
}

