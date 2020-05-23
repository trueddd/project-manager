package db.data.tasks

import db.data.User
import java.time.LocalDateTime

data class Worklog(
    val id: Int,
    val reporter: User,
    val workStartedAt: LocalDateTime,
    val workFinishedAt: LocalDateTime,
    val comment: String?
)