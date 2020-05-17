package db.data.tasks

data class TaskCreateBody(
    val name: String,
    val description: String?,
    val sprintId: Int,
    val executors: List<Int>?
)