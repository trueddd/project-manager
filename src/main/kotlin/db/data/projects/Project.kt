package db.data.projects

import db.data.teams.Team

data class Project(
    val id: Int,
    val name: String,
    val createdAt: Long,
    val team: Team?
)