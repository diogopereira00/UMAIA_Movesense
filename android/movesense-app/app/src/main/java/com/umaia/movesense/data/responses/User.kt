package com.umaia.movesense.data.responses

data class User(
    val access_token: String,
    val created_at: String,
    val id: String,
    val last_login: String,
    val isResearcher : Int,
    val username: String
)