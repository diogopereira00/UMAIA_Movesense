package com.umaia.movesense.data.repository

import com.umaia.movesense.data.network.ServerApi
import com.umaia.movesense.data.responses.UserPreferences

class ApiRepository(
    private val api: ServerApi,
    private val preferences: UserPreferences?
) : BaseRepository() {

    suspend fun getAllStudiesFromUser(userID: String,authToken: String) = safeApiCall{
        api.getStudies(userID,authToken)
    }

    suspend fun getAllOptions(authToken: String) = safeApiCall {
        api.getOptions(authToken)
    }

    suspend fun getAllQuestionTypes(authToken: String) = safeApiCall {
        api.getQuestionTypes(authToken)
    }

    suspend fun addAccData(jsonString: String, authToken : String) = safeApiCall{
        api.addAccData(jsonString, authToken)
    }
    suspend fun addMagnData(jsonString: String, authToken : String) = safeApiCall{
        api.addMagnData(jsonString, authToken)
    }
    suspend fun addGyroData(jsonString: String, authToken : String) = safeApiCall{
        api.addGyroData(jsonString, authToken)
    }
    suspend fun addEcgData(jsonString: String, authToken : String) = safeApiCall{
        api.addECGData(jsonString, authToken)
    }
    suspend fun addHrData(jsonString: String, authToken : String) = safeApiCall{
        api.addHrData(jsonString, authToken)
    }
    suspend fun addTempData(jsonString: String, authToken : String) = safeApiCall{
        api.addTempData(jsonString, authToken)
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