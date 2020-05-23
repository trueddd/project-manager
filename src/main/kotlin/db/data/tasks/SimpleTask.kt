package db.data.tasks

import db.data.projects.Project
import java.time.LocalDateTime

data class SimpleTask(
    val id: Int,
    val name: String,
    val description: String?,
    val createdAt: LocalDateTime,
    val sprint: Sprint,
    val project: Project
)