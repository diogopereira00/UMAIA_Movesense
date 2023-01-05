package com.umaia.movesense

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.umaia.movesense.data.network.Resource
import com.umaia.movesense.data.responses.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val datastore = UserPreferences(application)

    val getConsentStatus = datastore.liveData.asLiveData(Dispatchers.IO)
    fun setConsentStatus(isActivated: Boolean){
        viewModelScope.launch {
            datastore.saveConsentStatus(isActivated)
        }
    }


    val getLiveDataStatus = datastore.liveData.asLiveData(Dispatchers.IO)
    fun setLiveDataStatus(isActivated : Boolean){
        viewModelScope.launch {
            datastore.saveLiveDATAStatus(isActivated)

        }
    }


    val getAccStatus = datastore.accStatus.asLiveData(Dispatchers.IO)
    fun setAccStatus(isActivated : Boolean){
        viewModelScope.launch {
            datastore.saveAccStatus(isActivated)

        }
    }

    val getGyroStatus = datastore.gyroStatus.asLiveData(Dispatchers.IO)
    fun setGyroStatus(isActivated: Boolean){
        viewModelScope.launch {
            datastore.saveGyroStatus(isActivated)
        }
    }

    val getMagnStatus = datastore.magnStatus.asLiveData(Dispatchers.IO)
    fun setMagnStatus(isActivated: Boolean){
        viewModelScope.launch {
            datastore.saveMagnStatus(isActivated)
        }
    }
    val getImuStatus = datastore.imuStatus.asLiveData(Dispatchers.IO)
    fun setImuStatus(isActivated: Boolean){
        viewModelScope.launch {
            datastore.saveImuStatus(isActivated)
        }
    }

    val getECGStatus = datastore.ecgStatus.asLiveData(Dispatchers.IO)
    fun setECGStatus(isActivated: Boolean){
        viewModelScope.launch {
            datastore.saveEcgStatus(isActivated)
        }
    }

    val getHRStatus = datastore.hrStatus.asLiveData(Dispatchers.IO)
    fun setHRStatus(isActivated: Boolean){
        viewModelScope.launch {
            datastore.saveHrStatus(isActivated)
        }
    }

    val getTempStatus = datastore.tempStatus.asLiveData(Dispatchers.IO)
    fun setTempStatus(isActivated: Boolean){
        viewModelScope.launch {
            datastore.saveTempStatus(isActivated)
        }
    }

}