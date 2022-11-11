package com.umaia.movesense

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.umaia.movesense.util.Constants
import timber.log.Timber

class GlobalClass : Application() {
    var teste = ""
    var bluetoothList: ArrayList<MyScanResult> = ArrayList<MyScanResult>()
    lateinit var currentDevice : MyScanResult
    lateinit var notificationManager: NotificationManager

    @RequiresApi(Build.VERSION_CODES.O)
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
        }    }
}