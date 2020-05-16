package db.data.projects

import db.data.User

data class ProjectMember(
    val user: User,
    val isOwner: Boolean
)