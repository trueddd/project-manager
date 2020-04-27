package db.data

import db.data.teams.Team
import io.ktor.auth.Principal

data class User(
    val id: Int,
    val name: String,
    val firstName: String?,
    val lastName: String?,
    val team: Team?
) : Principal