package utils

import db.dao.Epics
import db.dao.Projects
import db.dao.Sprints
import db.dao.Teams
import db.data.projects.Project
import db.data.tasks.Epic
import db.data.tasks.Sprint
import db.data.teams.Team
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toProject(withTeam: Boolean = true): Project {
    return Project(
        this[Projects.id].toInt(),
        this[Projects.name].toString(),
        this[Projects.createdAt].toLong(),
        if (withTeam) {
            Team(
                this[Teams.id].toInt(),
                this[Teams.name].toString(),
                this[Teams.country]?.toString(),
                this[Teams.city]?.toString()
            )
        } else null
    )
}

fun ResultRow.toEpic(): Epic {
    return Epic(this[Epics.id], this[Epics.name])
}

fun ResultRow.toSprint(): Sprint {
    return Sprint(this[Sprints.id], this[Sprints.name])
}