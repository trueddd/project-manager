package utils

import db.dao.*
import db.data.User
import db.data.projects.Project
import db.data.tasks.Epic
import db.data.tasks.Sprint
import db.data.tasks.Task
import db.data.tasks.TaskState
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toProject(): Project {
    return Project(
        this[Projects.id].toInt(),
        this[Projects.name].toString(),
        this[Projects.createdAt].toLong()
    )
}

fun ResultRow.toEpic(): Epic {
    return Epic(this[Epics.id], this[Epics.name])
}

fun ResultRow.toSprint(): Sprint {
    return Sprint(this[Sprints.id], this[Sprints.name])
}

fun ResultRow.toUser(): User {
    return User(
        this[Users.id].toInt(),
        this[Users.name].toString(),
        this[Users.firstName]?.toString(),
        this[Users.lastName]?.toString()
    )
}

fun ResultRow.isOwner() = this[ProjectsUsers.rights].toInt() >= 300

fun ResultRow.toTask(): Task {
    return Task(
        this[Tasks.id].toInt(),
        this[Tasks.name].toString(),
        this[Tasks.description]?.toString(),
        this[Tasks.createdAt].toLong(),
        TaskState(this[TaskStates.id].toInt(), this[TaskStates.name].toString()),
        this.toSprint(),
        this.toUser()
    )
}