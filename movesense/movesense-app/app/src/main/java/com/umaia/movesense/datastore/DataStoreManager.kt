package com.umaia.movesense.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

class DataStoreManager(private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("CONNECTED_STATUS")

        val CONNECTED_STATUS_KEY = booleanPreferencesKey("CONNECTED_KEY")
    }

    suspend fun setStatus(isConnected: Boolean) {
        dataStore.edit { preferences -> preferences[CONNECTED_STATUS_KEY] = isConnected }

    }

    suspend fun read(key:String) : Boolean? {
        val preferences = dataStore.data.first()
        return preferences[CONNECTED_STATUS_KEY]
    }
    fun getStatus(): Flow<Boolean> {
        return dataStore.data.catch { ex ->
            if (ex is IOException) {
                emit(emptyPreferences())
            } else {
                throw ex
            }
        }.map { preferences ->
            val status = preferences[CONNECTED_STATUS_KEY] ?: false
            status
        }

    }
}