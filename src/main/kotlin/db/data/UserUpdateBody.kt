package db.data

data class UserUpdateBody(
    val name: String?,
    val firstName: String?,
    val lastName: String?,
    val phone: String?,
    val email: String?,
    val teamStatus: String?
)