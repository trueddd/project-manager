package utils

object Errors {

    object Users {

        object NameAlreadyUsed : Exception("Name is already used by another user")
    }

    object Teams {

        object OwnerApply : Exception("Couldn\'t apply owner to team")
    }

    object Unknown : Exception()
    object WrongPass : Exception("Wrong password")
    data class NotFound(val target: String) : Exception("${target.capFirst()} not found")
    data class Create(val target: String) : Exception("${target.capFirst()} creation error")
    data class Modify(val target: String) : Exception("${target.capFirst()} modification error")
    data class NoAccess(val target: String) : Exception("You have no access to this ${target.toLowerCase()}")
    data class Conflict(val target: String) : Exception("Conflict with another ${target.toLowerCase()}")
}