package com.umaia.movesense.util



object Constants {
    const val ACTION_START_SERVICE = "ACTION_START_SERVICE"
    const val ACTION_BLUETOOTH_CONNECTED = "ACTION_BLUETOOTH_CONNECTED"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_REFRESH_SERVICE = "ACTION_REFRESH_SERVICE"


    const val NOTIFICATION_CHANNEL_ID ="movesense_channel"
    const val NOTIFICATION_CHANNEL_NAME="movesense_channel"
    const val NOTIFICATION_ID = 143
    const val URI_MEAS_ACC_13 = "/Meas/Acc/13"
    const val URI_EVENTLISTENER = "suunto://MDS/EventListener"
    const val URI_MEAS_HR = "/Meas/HR"
    const val URI_MEAS_TEMP = "/Meas/Temp"
    const val URI_MEAS_IMU_9= "/Meas/IMU9/13"
    const val URI_MEAS_IMU_6M= "/Meas/IMU6m/13"
    const val URI_MEAS_IMU_6= "/Meas/IMU6/13"

    const val URI_MEAS_GYRO_13= "/Meas/Gyro/13"
    const val URI_MEAS_MAGN_13= "/Meas/Magn/13"

    const val MY_PERMISSIONS_REQUEST_LOCATION = 1
    const val URI_ECG_INFO = "/Meas/ECG/Info"
    const val URI_ECG_ROOT = "/Meas/ECG/"

    const val SETTINGS_SENSORS  = "Sensores"
    const val SETTINGS_LOGOUT = "Logou"

    val PREFERENCES = "preferences"
    val ECG_SCANNER_STATUS = "ecg_scanner_status"

}