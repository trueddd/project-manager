package utils

import db.data.UserOutput
import io.ktor.application.ApplicationCall
import io.ktor.auth.authentication
import io.ktor.request.receive

val ApplicationCall.user get() = authentication.principal<UserOutput>()!!

suspend inline fun <reified T : Any> ApplicationCall.receiveSafe(): T? {
    return try {
        receive<T>()
    } catch (e: Exception) {
        null
    }
}