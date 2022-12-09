package com.umaia.movesense.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.umaia.movesense.ApiViewModel
import com.umaia.movesense.data.repository.ApiRepository
import com.umaia.movesense.data.repository.BaseRepository

class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(ApiViewModel::class.java) -> ApiViewModel(repository as ApiRepository) as T

            else -> throw java.lang.IllegalArgumentException("ViewModelClass Not Found")

        }
    }
}