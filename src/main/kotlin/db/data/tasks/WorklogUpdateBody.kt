package db.data.tasks

import java.time.LocalDateTime

data class WorklogUpdateBody(
    val workStartedAt: LocalDateTime?,
    val workFinishedAt: LocalDateTime?,
    val comment: String?
)