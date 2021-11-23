package unibz.cs.semint.kprime.support

typealias FailureReason = String

fun SimpleGrantedAuthority(s: String) {
    TODO("Not yet implemented")
}

sealed class AuthenticationResult {
    data class Success(val group: LdapGroup) : AuthenticationResult()
    data class Failure(val reason: FailureReason) : AuthenticationResult()

    enum class LdapGroup {
        READONLY,
        NORMAL,
        ADMIN;

        // Authorities are a Spring Security concept
        fun toAuthority() = when (this) {
            ADMIN -> SimpleGrantedAuthority("ROLE_ADMIN")
            NORMAL -> SimpleGrantedAuthority("ROLE_NORMAL")
            READONLY -> SimpleGrantedAuthority("ROLE_READONLY")
        }

    }
    enum class FailureReason {
        BLANK_USER_OR_PW,
        INVALID_USER_OR_PW,
        USER_IS_NOT_IN_GROUP,
        CONNECTION_ISSUES
    }



}



object LdapDAO {
    fun authenticate(name:String, password:String):AuthenticationResult {
        if (name.startsWith("A"))
        return AuthenticationResult.Success(AuthenticationResult.LdapGroup.ADMIN)
        else
            return AuthenticationResult.Failure(
                    AuthenticationResult.FailureReason.INVALID_USER_OR_PW)
    }
}

fun check() {
    val name=""
    val password =""
    when (val result = LdapDAO.authenticate(name, password)) {
        is AuthenticationResult.Success -> createSession(name, result.group.toAuthority())
        is AuthenticationResult.Failure -> when(result.reason) {
            AuthenticationResult.FailureReason.INVALID_USER_OR_PW -> println("")
            else -> println("OTHER failure reasons.")
        }
    }.exhaustive

}




fun createSession(name: String, toAuthority: Any) {
    TODO("Not yet implemented")
}

fun displayMessageToTheUser(reason: AuthenticationResult.FailureReason) {
    TODO("Not yet implemented")
}
