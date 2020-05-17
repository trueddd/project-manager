package db.data.tasks

import db.data.User

data class Task(
    val id: Int,
    val name: String,
    val description: String?,
    val createdAt: Long,
    val state: TaskState,
    val sprint: Sprint,
    val creator: User,
    val executors: List<User> = emptyList(),
    val worklogs: List<Worklog> = emptyList()
)