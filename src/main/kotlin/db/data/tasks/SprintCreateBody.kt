package db.data.tasks

import java.time.LocalDateTime

data class SprintCreateBody(
    val name: String,
    val start: LocalDateTime,
    val finish: LocalDateTime
)