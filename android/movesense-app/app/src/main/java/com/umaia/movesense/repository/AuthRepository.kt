package com.umaia.movesense.repository

import com.umaia.movesense.network.AuthApi

class AuthRepository(
    private val api: AuthApi
) : BaseRepository() {

    suspend fun login(
        username: String,
        password: String
    ) = safeApiCall {
        api.login(username, password)
    }

}