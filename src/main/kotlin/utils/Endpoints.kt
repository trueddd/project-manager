package utils

object Endpoints {
    object Login {
        const val Register = "/register"
        const val Refresh = "/login"
    }
    object Users {
        const val Base = "/users"
    }
    object Teams {
        const val Base = "/teams"
        const val Members = "$Base/members"
    }
    object Projects {
        const val Base = "/projects"
    }
}