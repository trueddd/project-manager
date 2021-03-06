package utils

import db.dao.*
import db.data.User
import db.data.projects.Project
import db.data.tasks.*
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toProject(): Project {
    return Project(
        this[Projects.id].toInt(),
        this[Projects.name].toString(),
        this[Projects.createdAt],
        this[Projects.description]?.toString()
    )
}

fun ResultRow.toEpic(): Epic {
    return Epic(this[Epics.id], this[Epics.name])
}

fun ResultRow.toSprint(): Sprint {
    return Sprint(
        this[Sprints.id].toInt(),
        this[Sprints.name].toString(),
        this[Sprints.start],
        this[Sprints.finish]
    )
}

fun ResultRow.toUser(): User {
    return User(
        this[Users.id].toInt(),
        this[Users.name].toString(),
        this[Users.firstName]?.toString(),
        this[Users.lastName]?.toString(),
        this[Users.phone]?.toString(),
        this[Users.email]?.toString(),
        this[Users.teamStatus]?.toString()
    )
}

fun ResultRow.isOwner() = this[ProjectsUsers.rights].toInt() >= 300

fun ResultRow.toTask(executors: List<User>?, worklogs: List<Worklog>?): Task {
    return Task(
        this[Tasks.id].toInt(),
        this[Tasks.name].toString(),
        this[Tasks.description]?.toString(),
        this[Tasks.createdAt],
        TaskState(this[TaskStates.id].toInt(), this[TaskStates.name].toString()),
        this.toSprint(),
        this.toUser(),
        executors ?: emptyList(),
        worklogs ?: emptyList()
    )
}

fun ResultRow.toWorklog(): Worklog {
    return Worklog(
        this[WorkLogs.id].toInt(),
        this.toUser(),
        this[WorkLogs.startedAt],
        this[WorkLogs.finishedAt],
        this[WorkLogs.comment]?.toString()
    )
}

fun ResultRow.toWorklogStatsItem(): WorklogStatsItem {
    return WorklogStatsItem(
        this[WorkLogs.id],
        this[WorkLogs.comment]?.toString(),
        this.toSimpleTask(),
        this[WorkLogs.startedAt],
        this[WorkLogs.finishedAt]
    )
}

fun ResultRow.toSimpleTask(): SimpleTask {
    return SimpleTask(
        this[Tasks.id].toInt(),
        this[Tasks.name].toString(),
        this[Tasks.description]?.toString(),
        this[Tasks.createdAt],
        this.toSprint(),
        this.toProject()
    )
}

fun ResultRow.toExtendedWorklog(): ExtendedWorklog {
    return ExtendedWorklog(
        this[WorkLogs.id].toInt(),
        this.toUser(),
        this[WorkLogs.startedAt],
        this[WorkLogs.finishedAt],
        this[WorkLogs.comment]?.toString(),
        this.toSimpleTask()
    )
}