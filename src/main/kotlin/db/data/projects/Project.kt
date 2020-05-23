package db.data.projects

import java.time.LocalDateTime

data class Project(
    val id: Int,
    val name: String,
    val createdAt: LocalDateTime,
    val description: String?
)