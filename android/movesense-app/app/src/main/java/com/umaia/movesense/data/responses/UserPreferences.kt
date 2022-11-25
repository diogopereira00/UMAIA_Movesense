package com.umaia.movesense.data.responses

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class UserPreferences(
    context : Context
) {

    private val applicationContext = context.applicationContext

    val token : Flow<String?>
    get()  = applicationContext.dataStore.data.map { preferences ->
        preferences[KEY_AUTH]
    }

    suspend fun saveAuthToken (token : String){
        applicationContext.dataStore.edit { preferences ->
            preferences[KEY_AUTH] = token
        }
    }
    companion object{
        private val KEY_AUTH = stringPreferencesKey("key_auth")
    }


}