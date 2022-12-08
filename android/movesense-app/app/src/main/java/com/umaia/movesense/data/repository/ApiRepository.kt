package com.umaia.movesense.data.repository

import com.umaia.movesense.data.network.ServerApi
import com.umaia.movesense.data.responses.UserPreferences

class ApiRepository(
    private val api: ServerApi,
    private val preferences: UserPreferences?
) : BaseRepository() {


    suspend fun addAccData(jsonString: String, authToken : String) = safeApiCall{
        api.addAccData(jsonString, authToken)
    }

    suspend fun login(
        username: String,
        password: String
    ) = safeApiCall {
        api.login(username, password)
    }

    suspend fun saveUserID(id: String){
        preferences!!.saveUserID(id)
    }

    suspend fun saveAuthToken(token : String){
        preferences!!.saveAuthToken(token)
    }

    // TODO: Fazer logout na api
    suspend fun clearAuthToken(){
        preferences!!.clearAuthToken()
    }
}