package db.data

import io.ktor.auth.Principal

data class UserOutput(
    val id: Int,
    val name: String
) : Principal