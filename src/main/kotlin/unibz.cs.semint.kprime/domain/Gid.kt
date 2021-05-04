package unibz.cs.semint.kprime.domain

import java.util.*

// https://docs.oracle.com/javase/7/docs/api/java/util/UUID.html#randomUUID()
fun nextGid(): Gid = UUID.randomUUID().toString()

// hexDigit 0-9,a-z,A-Z,'-'
fun Gid.isValidGid(): Boolean {
    if (this.isEmpty()) return false
    if (this.length < 10) return false
    if (!this.matches(Regex("[0-9a-zA-Z\\-]+"))) return false
    return true
}

typealias  Gid = String