package db.data

data class UserCreateBody(
    val name: String,
    val pass: String,
    val firstName: String?,
    val lastName: String?,
    val phone: String?,
    val email: String?,
    val teamStatus: String?
)