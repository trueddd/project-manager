package db.data.teams

data class Team(
    val id: Int,
    val name: String,
    val country: String?,
    val city: String?
)