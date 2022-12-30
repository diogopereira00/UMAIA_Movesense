package com.umaia.movesense.util

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.umaia.movesense.ApiViewModel
import com.umaia.movesense.data.suveys.options.repository.ApiRepository
import com.umaia.movesense.data.suveys.options.repository.BaseRepository
import com.umaia.movesense.data.suveys.StudiesViewmodel

class ViewModelFactory(
    private val repository: BaseRepository?,
    private val context: Context? = null
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(ApiViewModel::class.java) -> ApiViewModel(repository as ApiRepository) as T
            modelClass.isAssignableFrom(StudiesViewmodel::class.java) -> StudiesViewmodel(context!!.applicationContext as Application) as T

            else -> throw java.lang.IllegalArgumentException("ViewModelClass Not Found")

        }
    }
}