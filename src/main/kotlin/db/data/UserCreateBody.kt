package db.data

data class UserCreateBody(
    val name: String,
    val pass: String,
    val firstName: String?,
    val lastName: String?
)