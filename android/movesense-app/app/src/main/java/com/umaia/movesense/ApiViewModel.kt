package com.umaia.movesense

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.umaia.movesense.data.network.Resource
import com.umaia.movesense.data.repository.ApiRepository
import com.umaia.movesense.data.responses.*
import com.umaia.movesense.data.responses.studies_response.OptionsResponse
import com.umaia.movesense.data.responses.studies_response.QuestionTypesResponse
import com.umaia.movesense.data.responses.studies_response.QuestionsOptionsResponses
import com.umaia.movesense.data.responses.studies_response.StudiesResponse
import com.umaia.movesense.data.suveys.questions_types.QuestionTypes
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

    //getAllStudiesFromUserId
    private val _getAllStudiesFromUserIDReponse: MutableLiveData<Resource<StudiesResponse>> = MutableLiveData()
    val getAllStudiesFromUserIDReponse: LiveData<Resource<StudiesResponse>>
        get() = _getAllStudiesFromUserIDReponse
    fun getAllStudiesFromId(
        userID: String,
        authToken : String
    ) = viewModelScope.launch {
        _getAllStudiesFromUserIDReponse.value = repository.getAllStudiesFromUser(userID,authToken= "Bearer $authToken")
    }

    //getOptions
    private val _getOptionsReponse: MutableLiveData<Resource<OptionsResponse>> = MutableLiveData()
    val getOptionsReponse: LiveData<Resource<OptionsResponse>>
        get() = _getOptionsReponse
    fun getOptions(
        authToken : String
    ) = viewModelScope.launch {
        _getOptionsReponse.value = repository.getAllOptions(authToken= "Bearer $authToken")
    }
    //getQuestionTypes
    private val _getQuestionTypes: MutableLiveData<Resource<QuestionTypesResponse>> = MutableLiveData()
    val getQuestionTypes: LiveData<Resource<QuestionTypesResponse>>
        get() = _getQuestionTypes
    fun getQuestionTypes(
        authToken : String
    ) = viewModelScope.launch {
        _getQuestionTypes.value = repository.getAllQuestionTypes(authToken= "Bearer $authToken")
    }

    //getQuestionTypes
    private val _getQuestionOptions: MutableLiveData<Resource<QuestionsOptionsResponses>> = MutableLiveData()
    val getQuestionOptions: LiveData<Resource<QuestionsOptionsResponses>>
        get() = _getQuestionOptions
    fun getQuestionOptions(
        authToken : String
    ) = viewModelScope.launch {
        _getQuestionOptions.value = repository.getAllQuestionsOptions(authToken= "Bearer $authToken")
    }


    //Data ACC
    private val _uploadDataAccResponses: MutableLiveData<Resource<UploadAccRespose>> = MutableLiveData()
    val uploadDataAccResponses: LiveData<Resource<UploadAccRespose>>
        get() = _uploadDataAccResponses

    fun addACCData(jsonString: String, authToken : String) = viewModelScope.launch {
        _uploadDataAccResponses.value = repository.addAccData(jsonString= jsonString,authToken= "Bearer $authToken")
    }

    //Data Magn
    private val _uploadDataMagnResponses: MutableLiveData<Resource<UploadMagnRespose>> = MutableLiveData()
    val uploadDataMagnResponses: LiveData<Resource<UploadMagnRespose>>
        get() = _uploadDataMagnResponses

    fun addMagnData(jsonString: String, authToken : String) = viewModelScope.launch {
        _uploadDataMagnResponses.value = repository.addMagnData(jsonString= jsonString,authToken= "Bearer $authToken")
    }

    //Data Gyro
    private val _uploadDataGyroResponses: MutableLiveData<Resource<UploadGyroRespose>> = MutableLiveData()
    val uploadDataGyroResponses: LiveData<Resource<UploadGyroRespose>>
        get() = _uploadDataGyroResponses

    fun addGyroData(jsonString: String, authToken : String) = viewModelScope.launch {
        _uploadDataGyroResponses.value = repository.addGyroData(jsonString= jsonString,authToken= "Bearer $authToken")
    }

    //Data ECG
    private val _uploadDataECGResponses: MutableLiveData<Resource<UploadECGRespose>> = MutableLiveData()
    val uploadDataECGResponses: LiveData<Resource<UploadECGRespose>>
        get() = _uploadDataECGResponses

    fun addECGData(jsonString: String, authToken : String) = viewModelScope.launch {
        _uploadDataECGResponses.value = repository.addEcgData(jsonString= jsonString,authToken= "Bearer $authToken")
    }

    //Data HR
    private val _uploadDataHRResponses: MutableLiveData<Resource<UploadHrRespose>> = MutableLiveData()
    val uploadDataHRResponses: LiveData<Resource<UploadHrRespose>>
        get() = _uploadDataHRResponses

    fun addHRData(jsonString: String, authToken : String) = viewModelScope.launch {
        _uploadDataHRResponses.value = repository.addHrData(jsonString= jsonString,authToken= "Bearer $authToken")
    }

    //Data Temp todo
    private val _uploadDataTempResponses: MutableLiveData<Resource<UploadTempResponse>> = MutableLiveData()
    val uploadDataTempResponses: LiveData<Resource<UploadTempResponse>>
        get() = _uploadDataTempResponses

    fun addTempData(jsonString: String, authToken : String) = viewModelScope.launch {
        _uploadDataTempResponses.value = repository.addTempData(jsonString= jsonString,authToken= "Bearer $authToken")
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