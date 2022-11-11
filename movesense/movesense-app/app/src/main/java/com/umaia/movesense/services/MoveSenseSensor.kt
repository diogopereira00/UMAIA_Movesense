package com.umaia.movesense.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import com.movesense.mds.Mds
import com.movesense.mds.MdsConnectionListener
import com.movesense.mds.MdsException
import com.polidea.rxandroidble2.RxBleClient
import com.umaia.movesense.*
import com.umaia.movesense.model.MoveSenseEvent
import com.umaia.movesense.util.Constants
import timber.log.Timber

class MoveSenseSensor : LifecycleService() {

    companion object {
        val moveSenseEvent = MutableLiveData<MoveSenseEvent>()
    }

    private var isServiceStopped = false
    private var isConnected = false

    lateinit var notificationManager: NotificationManager
    private lateinit var notification: Notification

    private var bluetoothList: ArrayList<MyScanResult> = ArrayList<MyScanResult>()
    private var mBleClient: RxBleClient? = null
    private var mMds: Mds? = null
    lateinit var gv: GlobalClass

    override fun onCreate() {
        super.onCreate()

        gv = this.applicationContext as GlobalClass
        bluetoothList = gv.bluetoothList


    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                Constants.ACTION_START_SERVICE -> {
                    Timber.d("Started Service")
                    if (gv.currentDevice.isConnected) {
                        startForegroundService()
//                            createNotification2()

                    } else {
                        Timber.e("Errooooooooooooooooooooou")
                    }
                }
                Constants.ACTION_STOP_SERVICE -> {
                    Timber.d("Stop service")
                    stopService()
                }
                Constants.ACTION_BLUETOOTH_CONNECTED -> {
                    initMds()
                    connectBLEDevice(gv.currentDevice)

                }
            }


        }


        return super.onStartCommand(intent, flags, startId)
    }

    private fun initValues() {
        moveSenseEvent.postValue(MoveSenseEvent.STOP)
    }

    private fun startForegroundService() {
        moveSenseEvent.postValue(MoveSenseEvent.START)

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            createNotificationChannel()
//        }

//        startForeground(Constants.NOTIFICATION_ID, notification)

    }

    private fun stopService() {
        moveSenseEvent.postValue(MoveSenseEvent.STOP)
        isServiceStopped = true
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancel(
            Constants.NOTIFICATION_ID
        )
        stopForeground(true)
//        stopSelf()

    }

    private fun getBleClient(): RxBleClient? {
        // Init RxAndroidBle (Ble helper library) if not yet initialized
        if (mBleClient == null) {
            mBleClient = RxBleClient.create(this)
        }
        return mBleClient
    }

    private fun initMds() {
        if (mMds == null) {
            mMds = Mds.builder().build(this)
        }
    }

    private fun connectBLEDevice(device: MyScanResult) {
        val bleDevice = getBleClient()!!.getBleDevice(device.macAddress)
        Timber.e("Connecting to BLE device: " + bleDevice.macAddress)

        mMds!!.connect(bleDevice.macAddress, object : MdsConnectionListener {
            override fun onConnect(s: String) {
                Timber.e("onConnect:$s")
            }

            override fun onConnectionComplete(macAddress: String, serial: String) {
                //Cria notificação, sensor conectado.
                isConnected = true
                createNotification2()

                for (sr in bluetoothList) {
                    if (sr.macAddress.equals(macAddress, true)) {
                        sr.markConnected(serial)
                        break
                    }
                }
//                mScanResArrayAdapter.notifyDataSetChanged()

                Toast.makeText(
                    this@MoveSenseSensor,
                    "Conectado ao sensor ${serial}.",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this@MoveSenseSensor, Main::class.java)
                intent.putExtra(ECGActivity().SERIAL, serial)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)


            }


            override fun onError(e: MdsException) {
                Timber.e("onError:$e")

                showConnectionError(e)
            }

            override fun onDisconnect(bleAddress: String) {
                Timber.e("onDisconnect: $bleAddress")
                isConnected = false
                createNotification2()
                Toast.makeText(this@MoveSenseSensor, "DESCONECTADOz<x<z.", Toast.LENGTH_SHORT).show()

                for (sr in bluetoothList) {
                    if (bleAddress == sr.macAddress) {
//                        saveConnectionStatus(false)

                        // Unsubscribe all from possible
                        if (sr.connectedSerial != null && Main.s_INSTANCE != null &&
                            sr.connectedSerial.equals(gv.currentDevice.connectedSerial)
                        ) {
                            unsubscribeAll()
                            Main.s_INSTANCE!!.finish()
//                            stopService(Intent(this@ScanDevice,MyService::class.java))
                        }
                        sr.markDisconnected()
                    }
                }
//                mScanResArrayAdapter.notifyDataSetChanged()
            }
        })
    }


//    private fun unsubscribeHR() {
//        if (mHRSubscription != null) {
//            mHRSubscription!!.unsubscribe()
//            mHRSubscription = null
//        }
//    }

    fun unsubscribeAll() {
        Timber.e("unsubscribeAll()")
        // TODO: asdasd
//        unsubscribeECG()
//        unsubscribeHR()
    }

    private fun showConnectionError(e: MdsException) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setTitle("Connection Error:")
            .setMessage(e.message)
        builder.create().show()
    }

    private fun createNotification2() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            moveSenseEvent.observe(this, Observer {
                when (it) {
                    is MoveSenseEvent.START -> {

                        var intent1 = Intent(this, Main::class.java)
                        var title = "Sensor conectado"
                        var description = "Recolhendo dados..."
                        var icon = R.mipmap.ic_umaia_logo
                        if (!isConnected) {
                            intent1 = Intent(this, ScanActivity::class.java)
                            title = "Sensor desconectado"
                            description = "Por favor verifique a conexão..."
                            icon = R.mipmap.ic_umaia_red_logo
                        }

                        var pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_MUTABLE)
                        } else {
                            PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_ONE_SHOT)
                        }
                        notification =
                            NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
                                .setContentTitle(title)
                                .setContentText(description)
                                .setSmallIcon(icon)
                                .setPriority(128)
                                .setContentIntent(pendingIntent).build()

                        startForeground(Constants.NOTIFICATION_ID, notification)

                    }

                    is MoveSenseEvent.STOP -> {

                    }
                }
            })
        }
    }


}