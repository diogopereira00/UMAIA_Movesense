package com.umaia.movesense

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umaia.movesense.data.network.Resource
import com.umaia.movesense.data.repository.ApiRepository
import com.umaia.movesense.data.responses.LoginResponse
import com.umaia.movesense.data.responses.UploadAccRespose
import kotlinx.coroutines.launch

class ApiViewModel(
    private val repository: ApiRepository
) : ViewModel() {

    //Login
    private val _loginResponse: MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse
    fun login(
        username: String,
        password: String
    ) = viewModelScope.launch {
        _loginResponse.value = repository.login(username, password)
    }

    //Data
    private val _uploadDataAccResponses: MutableLiveData<Resource<UploadAccRespose>> = MutableLiveData()
    val uploadDataAccResponses: LiveData<Resource<UploadAccRespose>>
        get() = _uploadDataAccResponses

    fun addACCData(jsonString: String, authToken : String) = viewModelScope.launch {
        _uploadDataAccResponses.value = repository.addAccData(jsonString= jsonString,authToken= "Bearer $authToken")
    }


    fun saveUserID(id: String) = viewModelScope.launch {
        repository.saveUserID(id)
        if (!id.isNullOrEmpty()) {
            gv.userID = id
        }
    }

    fun saveAuthToken(token: String) = viewModelScope.launch {
        repository.saveAuthToken(token)
    }

    fun clearAuthToken() = viewModelScope.launch {
        repository.clearAuthToken()
    }

}