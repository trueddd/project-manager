package db

data class UserLoginRequest(
    val name: String,
    val pass: String
)