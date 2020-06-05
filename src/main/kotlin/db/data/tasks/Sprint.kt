package db.data.tasks

import java.time.LocalDateTime

data class Sprint(
    val id: Int,
    val name: String,
    val start: LocalDateTime,
    val finish: LocalDateTime
)