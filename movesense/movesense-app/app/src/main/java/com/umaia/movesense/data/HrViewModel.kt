package com.umaia.movesense.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HrViewModel(application: Application) : AndroidViewModel(application) {
    private val readAllData : LiveData<List<Hr>>
    private val repository : HrRepository


    init {
        val hrDao = AppDataBase.getDatabase(application).hrDao()
        repository = HrRepository(hrDao)
        readAllData = repository.readAllData
    }

    fun addHr(hr: Hr){
        viewModelScope.launch(Dispatchers.IO){
            repository.addHr(hr)
        }
    }
}