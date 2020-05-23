package db.data.tasks

import java.time.LocalDateTime

data class WorklogStatsItem(
    val id: Int,
    val comment: String?,
    val task: SimpleTask,
    val workStartedAt: LocalDateTime,
    val workFinishedAt: LocalDateTime
)