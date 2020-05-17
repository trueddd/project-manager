package utils

object Endpoints {
    object Login {
        const val Register = "/register"
        const val Refresh = "/login"
    }
    const val Projects = "/projects"
    const val Users = "/users"
    const val Epics = "/epics"
    const val Sprints = "/sprints"
    const val States = "/states"
    const val Tasks = "/tasks"
    const val Worklogs = "/worklogs"
}

fun String.path(parameterName: String): String {
    return "$this/{$parameterName}"
}

fun String.route(routeName: String): String {
    return "$this/$routeName"
}