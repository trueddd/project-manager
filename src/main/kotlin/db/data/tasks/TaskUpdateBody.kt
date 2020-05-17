package db.data.tasks

data class TaskUpdateBody(
    val name: String?,
    val description: String?,
    val stateId: Int?,
    val sprintId: Int?,
    val addExecutors: List<Int>?,
    val removeExecutors: List<Int>?
)