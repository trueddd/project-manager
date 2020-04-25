package utils

object Errors {

    object Users {

        object NameAlreadyUsed : Exception("Name is already used by another user")
    }

    object Unknown : Exception()
    data class NotFound(val target: String) : Exception("$target not found")
    data class Create(val target: String) : Exception("$target creation error")
    data class Modify(val target: String) : Exception("$target modification error")
}