package com.umaia.movesense.datastore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataStoreViewModel(application: Application) : AndroidViewModel(application) {

    val dataStore = DataStoreManager(application)
    val getStatus = dataStore.getStatus().asLiveData(Dispatchers.IO)

    fun setStatus( isConnected : Boolean){
        viewModelScope.launch {
            dataStore.setStatus(isConnected)
        }
    }
}