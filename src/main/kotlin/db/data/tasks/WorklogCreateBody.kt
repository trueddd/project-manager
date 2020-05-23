package db.data.tasks

import java.time.LocalDateTime

data class WorklogCreateBody(
    val workStartedAt: LocalDateTime,
    val workFinishedAt: LocalDateTime,
    val comment: String?
)