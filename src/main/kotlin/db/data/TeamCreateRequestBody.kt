package db.data

data class TeamCreateRequestBody(
    val name: String,
    val country: String?,
    val city: String?
)