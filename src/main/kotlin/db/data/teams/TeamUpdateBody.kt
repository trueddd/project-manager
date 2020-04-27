package db.data.teams

data class TeamUpdateBody(
    val id: Int,
    val name: String?,
    val country: String?,
    val city: String?
)