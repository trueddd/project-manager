package utils

import db.data.User
import io.ktor.application.ApplicationCall
import io.ktor.auth.authentication
import io.ktor.request.receive

val ApplicationCall.user get() = authentication.principal<User>()

suspend inline fun <reified T : Any> ApplicationCall.receiveSafe(): T? {
    return try {
        receive<T>()
    } catch (e: Exception) {
        null
    }
}

fun String.capFirst(): String {
    return when (length) {
        0 -> ""
        1 -> this.capitalize()
        else -> "${this.first().toString().capitalize()}${this.substring(1)}"
    }
}