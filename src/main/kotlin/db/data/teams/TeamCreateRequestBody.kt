package db.data.teams

data class TeamCreateRequestBody(
    val name: String,
    val country: String?,
    val city: String?
)