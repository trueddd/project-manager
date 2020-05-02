package utils

import db.data.User
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.authentication
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.util.pipeline.PipelineContext

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

suspend fun <T> PipelineContext<*, ApplicationCall>.respondError(error: ServiceResult.Error<T>) {
    val statusCode = when (error.e) {
        is Errors.NoAccess, is Errors.WrongPass -> HttpStatusCode.Forbidden
        is Errors.NotFound -> HttpStatusCode.NotFound
        is Errors.Conflict, is Errors.Users.NameAlreadyUsed -> HttpStatusCode.Conflict
        else -> HttpStatusCode.InternalServerError
    }
    call.respond(statusCode, error.errorMessage())
}