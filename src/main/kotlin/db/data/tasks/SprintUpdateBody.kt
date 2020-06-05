package db.data.tasks

import java.time.LocalDateTime

data class SprintUpdateBody(
    val name: String?,
    val start: LocalDateTime?,
    val finish: LocalDateTime?
)