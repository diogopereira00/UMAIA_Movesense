package com.umaia.movesense.data.repository

import com.umaia.movesense.data.network.AuthApi
import com.umaia.movesense.data.responses.UserPreferences

class AuthRepository(
    private val api: AuthApi,
    private val preferences: UserPreferences
) : BaseRepository() {

    suspend fun login(
        username: String,
        password: String
    ) = safeApiCall {
        api.login(username, password)
    }

    suspend fun saveAuthToken(token : String){
        preferences.saveAuthToken(token)
    }
}