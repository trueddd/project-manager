package db.data.tasks

data class WorklogUpdateBody(
    val workStartedAt: Long?,
    val workDuration: Long?,
    val comment: String?
)