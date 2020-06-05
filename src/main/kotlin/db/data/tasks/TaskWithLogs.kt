package db.data.tasks

data class TaskWithLogs(
    val task: SimpleTask,
    val worklogs: List<Worklog>
)