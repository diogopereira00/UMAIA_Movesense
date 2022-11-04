package com.umaia.movesense.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.datastore.dataStore
import androidx.lifecycle.*
import com.umaia.movesense.R
import com.umaia.movesense.ScanDevice
import com.umaia.movesense.datastore.DataStoreManager
import com.umaia.movesense.datastore.DataStoreViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyService2 : Service(), LifecycleOwner {
    private var isConnected: Boolean = false
    private lateinit var viewModel: DataStoreViewModel
    private var status: String = ""
    private var testeee: String = ""

    private val mServiceLifecycleDispatcher = ServiceLifecycleDispatcher(this)


    suspend fun teste(): Boolean? {
        val dataStore = DataStoreManager(this@MyService2)

        return dataStore.read("CONNECTED_KEY")


    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val intent1 = Intent(this, ScanDevice::class.java)
        lifecycleScope.launch {
            val dataStore = DataStoreManager(this@MyService2)
            val isConnected = dataStore.read("CONNECTED_KEY")

            status = if (isConnected == true) {
                "Conectado"
            } else {
                "Desconectado"
            }
        }
//        viewModel = DataStoreViewModel(this.application)
//
//        viewModel.getStatus.observe(this) { isConnected ->
//            status = if (isConnected == true) {
//                "Conectado"
//            } else {
//                "False"
//            }
//        }



        isConnected = intent.getBooleanExtra("isConnected", false)
        var pendingIntent: PendingIntent? = null
        pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_ONE_SHOT)
        }

        val notification = NotificationCompat.Builder(this, "Channel1")

            .setContentTitle("UMAIA Movesense")
            .setContentText(status)
            .setSmallIcon(R.mipmap.ic_umaia_logo)
            .setContentIntent(pendingIntent).build()
        startForeground(1, notification)
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "Channel1", "Foreground Service", NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        stopForeground(true)
        stopSelf()
        super.onDestroy()
    }

    override fun getLifecycle(): Lifecycle {
        return mServiceLifecycleDispatcher.lifecycle
    }


}