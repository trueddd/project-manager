package db.data.tasks

data class WorklogCreateBody(
    val workStartedAt: Long,
    val workDuration: Long,
    val comment: String?
)