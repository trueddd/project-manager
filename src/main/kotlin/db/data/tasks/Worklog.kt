package db.data.tasks

import db.data.User

data class Worklog(
    val id: Int,
    val reporter: User,
    val workStartedAt: Long,
    val workDuration: Long,
    val comment: String?
)