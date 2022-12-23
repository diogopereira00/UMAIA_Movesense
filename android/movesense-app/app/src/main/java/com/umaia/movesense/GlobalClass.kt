package com.umaia.movesense

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.text.BoringLayout
import com.umaia.movesense.data.responses.studies_response.Survey
import com.umaia.movesense.util.Constants
import timber.log.Timber

class GlobalClass : Application() {




    var bluetoothList: ArrayList<MyScanResult> = ArrayList<MyScanResult>()
    lateinit var currentDevice: MyScanResult
    var connected : Boolean = false
    var hrAvarage = ""
    var hrRRdata = ""
    lateinit var notificationManager: NotificationManager


    var isServiceRunning : Boolean = false
    var isAccActivated : Boolean = false
    var isGyroActivated : Boolean = false
    var isMagnActivated : Boolean = false
    var isECGActivated : Boolean = false
    var isHRActivated : Boolean = false
    var isTempActivated : Boolean = false
    var isImuActivated : Boolean = false
    var isLiveDataActivated : Boolean = false

    var userID = ""
    var authToken = ""

    lateinit var currentSurvey : Survey

    private var scannerECG: Boolean ? = null
    fun getscannerECG(): Boolean? {
        return scannerECG
    }

    fun setscannerECG(scannerECG: Boolean) {
        this.scannerECG = scannerECG
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.NOTIFICATION_CHANNEL_ID,
                Constants.NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }
}