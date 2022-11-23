package com.umaia.movesense.util

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.umaia.movesense.AuthViewModel
import com.umaia.movesense.repository.AuthRepository
import com.umaia.movesense.repository.BaseRepository

class ViewModelFactory(
    private val repository: BaseRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(repository as AuthRepository) as T

            else -> throw java.lang.IllegalArgumentException("ViewModelClass Not Found")

        }
    }
}