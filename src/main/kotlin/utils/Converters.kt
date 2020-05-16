package utils

import db.dao.Epics
import db.dao.Projects
import db.dao.Sprints
import db.data.projects.Project
import db.data.tasks.Epic
import db.data.tasks.Sprint
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