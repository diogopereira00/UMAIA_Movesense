package com.umaia.movesense.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.RecyclerView
import com.movesense.mds.Mds
import com.movesense.mds.MdsConnectionListener
import com.movesense.mds.MdsException
import com.polidea.rxandroidble2.RxBleClient
import com.umaia.movesense.*
import com.umaia.movesense.adapters.BluetoothAdapter


class MyService : Service() {

    private val LOG_TAG = MyService::class.java.simpleName


    private var bluetoothList: ArrayList<MyScanResult> = ArrayList<MyScanResult>()
    private lateinit var mScanResArrayAdapter: BluetoothAdapter
    private lateinit var mScanResultRecyclerView: RecyclerView

    private var mBleClient: RxBleClient? = null
    private var mMds: Mds? = null
    private lateinit var notification: Notification

    lateinit var gv: GlobalClass
    override fun onStartCommand(init: Intent, flag: Int, startId: Int): Int {
        gv = this.applicationContext as GlobalClass

        createNotificationChannel()
        gv.teste = "ola"
        initMds()
//        mScanResArrayAdapter = init.getSerializableExtra("mScanResArrayAdapter") as BluetoothAdapter
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: View = inflater.inflate(R.layout.activity_scan_device, null)
        mScanResultRecyclerView = layout.findViewById(R.id.recyclerView)
        mScanResArrayAdapter = BluetoothAdapter(bluetoothList)
        mScanResultRecyclerView.adapter = mScanResArrayAdapter

        bluetoothList = gv.bluetoothList

        connectBLEDevice(gv.currentDevice)

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel("Channel1", "ASD", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(notificationChannel)
        }
    }


    fun createNotification(isConnected: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var intent1 = Intent(this, ECGActivity::class.java)
            var title = "Sensor conectado"
            var description = "Recolhendo dados..."
            var icon = R.mipmap.ic_umaia_logo
            if (!isConnected) {
                intent1 = Intent(this, ScanDevice::class.java)
                title = "Sensor desconectado"
                description = "Por favor verifique a conexão..."
                icon = R.mipmap.ic_umaia_red_logo
            }

            var pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_ONE_SHOT)
            }
            notification = NotificationCompat.Builder(this, "Channel1")
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(icon)
                .setPriority(128)
                .setContentIntent(pendingIntent).build()

            startForeground(1, notification)

        }
    }



    private fun connectBLEDevice(device: MyScanResult) {
        val bleDevice = getBleClient()!!.getBleDevice(device.macAddress)
        Log.i(LOG_TAG, "Connecting to BLE device: " + bleDevice.macAddress)
        mMds!!.connect(bleDevice.macAddress, object : MdsConnectionListener {
            override fun onConnect(s: String) {
                Log.d(LOG_TAG, "onConnect:$s")
            }

            override fun onConnectionComplete(macAddress: String, serial: String) {
                //Cria notificação, sensor conectado.
                createNotification(true)
                for (sr in bluetoothList) {
                    if (sr.macAddress.equals(macAddress, true)) {
                        sr.markConnected(serial)
                        break
                    }
                }
//                mScanResArrayAdapter.notifyDataSetChanged()

                Toast.makeText(this@MyService, "Conectado ao sensor ${serial}.", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@MyService, ECGActivity::class.java)
                intent.putExtra(ECGActivity().SERIAL, serial)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

            override fun onError(e: MdsException) {
                Log.e(LOG_TAG, "onError:$e")
                showConnectionError(e)
            }

            override fun onDisconnect(bleAddress: String) {

                Log.d(LOG_TAG, "onDisconnect: $bleAddress")
                createNotification(false)
                Toast.makeText(this@MyService, "DESCONECTADO.", Toast.LENGTH_SHORT).show()
//                if (Build.VERSION.SDK_INT >= 0) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        startForegroundService(Intent(this@MyService, MyService2::class.java))
//                    }
//                } else {
//                    stopService(Intent(this@MyService, MyService2::class.java))
//                }

                for (sr in bluetoothList) {
                    if (bleAddress == sr.macAddress) {
//                        saveConnectionStatus(false)

                        // Unsubscribe all from possible
                        if (sr.connectedSerial != null && ECGActivity.s_INSTANCE != null &&
                            sr.connectedSerial.equals(ECGActivity.s_INSTANCE!!.connectedSerial)
                        ) {
                            ECGActivity.s_INSTANCE!!.unsubscribeAll()
                            ECGActivity.s_INSTANCE!!.finish()
//                            stopService(Intent(this@ScanDevice,MyService::class.java))
                        }
                        sr.markDisconnected()
                    }
                }
//                mScanResArrayAdapter.notifyDataSetChanged()
            }
        })
    }


    private fun showConnectionError(e: MdsException) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setTitle("Connection Error:")
            .setMessage(e.message)
        builder.create().show()
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

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}