package com.umaia.movesense.data.responses

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class UserPreferences(
    context: Context
) {

    private val applicationContext = context.applicationContext


    //Auth Token
    val token: Flow<String?>
        get() = applicationContext.dataStore.data.map { preferences ->
            preferences[KEY_AUTH]
        }

    suspend fun saveAuthToken(token: String) {
        applicationContext.dataStore.edit { preferences ->
            preferences[KEY_AUTH] = token
        }
    }

    suspend fun clearAuthToken() {
        applicationContext.dataStore.edit { preferences ->
            preferences[KEY_AUTH] = ""
        }
    }
    //User ID
    val userid: Flow<String?>
        get() = applicationContext.dataStore.data.map { preferences ->
            preferences[USER_ID]
        }

    suspend fun saveUserID(id: String) {
        applicationContext.dataStore.edit { preferences ->
            preferences[USER_ID] = id
        }
    }

    suspend fun clearUserID() {
        applicationContext.dataStore.edit { preferences ->
            preferences[USER_ID] = ""
        }
    }

    //Acc status
    val accStatus: Flow<Boolean?>
        get() = applicationContext.dataStore.data.map { preferences ->
            preferences[KEY_ACC_STATUS]
        }

    suspend fun saveAccStatus(isActivated: Boolean) {
        applicationContext.dataStore.edit { preferences ->
            preferences[KEY_ACC_STATUS] = isActivated
        }
    }

    //Gyro status
    val gyroStatus: Flow<Boolean?>
        get() = applicationContext.dataStore.data.map { preferences ->
            preferences[KEY_GYRO_STATUS]
        }

    suspend fun saveGyroStatus(isActivated: Boolean) {
        applicationContext.dataStore.edit { preferences ->
            preferences[KEY_GYRO_STATUS] = isActivated
        }
    }
    //magn status
    val magnStatus: Flow<Boolean?>
        get() = applicationContext.dataStore.data.map { preferences ->
            preferences[KEY_MAGN_STATUS]
        }

    suspend fun saveMagnStatus(isActivated: Boolean) {
        applicationContext.dataStore.edit { preferences ->
            preferences[KEY_MAGN_STATUS] = isActivated
        }
    }
    //imu status
    val imuStatus: Flow<Boolean?>
        get() = applicationContext.dataStore.data.map { preferences ->
            preferences[KEY_IMU_STATUS]
        }

    suspend fun saveImuStatus(isActivated: Boolean) {
        applicationContext.dataStore.edit { preferences ->
            preferences[KEY_IMU_STATUS] = isActivated
        }
    }

    //ecg status
    val ecgStatus: Flow<Boolean?>
        get() = applicationContext.dataStore.data.map { preferences ->
            preferences[KEY_ECG_STATUS]
        }

    suspend fun saveEcgStatus(isActivated: Boolean) {
        applicationContext.dataStore.edit { preferences ->
            preferences[KEY_ECG_STATUS] = isActivated
        }
    }
    //hr status
    val hrStatus: Flow<Boolean?>
        get() = applicationContext.dataStore.data.map { preferences ->
            preferences[KEY_HR_STATUS]
        }

    suspend fun saveHrStatus(isActivated: Boolean) {
        applicationContext.dataStore.edit { preferences ->
            preferences[KEY_HR_STATUS] = isActivated
        }
    }
    //hr status
    val tempStatus: Flow<Boolean?>
        get() = applicationContext.dataStore.data.map { preferences ->
            preferences[KEY_TEMP_STATUS]
        }

    suspend fun saveTempStatus(isActivated: Boolean) {
        applicationContext.dataStore.edit { preferences ->
            preferences[KEY_TEMP_STATUS] = isActivated
        }
    }
    companion object {
        private val USER_ID = stringPreferencesKey("user_id")
        private val KEY_AUTH = stringPreferencesKey("key_auth")
        private val KEY_ACC_STATUS = booleanPreferencesKey("key_acc_status")
        private val KEY_GYRO_STATUS = booleanPreferencesKey("key_gyro_status")
        private val KEY_MAGN_STATUS = booleanPreferencesKey("key_magn_status")
        private val KEY_IMU_STATUS = booleanPreferencesKey("key_imu_status")
        private val KEY_ECG_STATUS = booleanPreferencesKey("key_ecg_status")
        private val KEY_HR_STATUS = booleanPreferencesKey("key_hr_status")
        private val KEY_TEMP_STATUS = booleanPreferencesKey("key_temp_status")


    }


}