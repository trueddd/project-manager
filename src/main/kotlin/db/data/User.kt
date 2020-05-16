package db.data

import io.ktor.auth.Principal

data class User(
    val id: Int,
    val name: String,
    val firstName: String?,
    val lastName: String?
) : Principal