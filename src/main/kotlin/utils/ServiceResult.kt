package utils

sealed class ServiceResult<T> {

    data class Success<T>(val data: T) : ServiceResult<T>()

    data class Error<T>(val e: Exception) : ServiceResult<T>()

    fun getSuccess(): T? = if (this is Success) data else null
}

fun <T> T.success() = ServiceResult.Success(this)

fun <T, E : Exception> E.error() = ServiceResult.Error<T>(this)

fun <T> ServiceResult.Error<T>.errorMessage(): String {
    return e.message.orEmpty()
}