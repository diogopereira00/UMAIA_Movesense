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
    private var status: String = ""


    override fun onStartCommand(init: Intent, flag: Int, startId: Int): Int {

        initMds()
//        mScanResArrayAdapter = init.getSerializableExtra("mScanResArrayAdapter") as BluetoothAdapter
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: View = inflater.inflate(R.layout.activity_scan_device, null)
        mScanResultRecyclerView = layout.findViewById(R.id.recyclerView)
        mScanResArrayAdapter = BluetoothAdapter(bluetoothList)
        mScanResultRecyclerView.adapter = mScanResArrayAdapter

        bluetoothList = init.getSerializableExtra("bluetoothList") as ArrayList<MyScanResult>

        var device = init.getSerializableExtra("device") as MyScanResult
        connectBLEDevice(device)

        return START_STICKY
    }

    fun createNotification(isConnected: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (isConnected) {
                val intent1 = Intent(this, ECGActivity::class.java)
                //TODO IF ACTIVITY IS OPEN DONT OPEN AGAIN
                var pendingIntent: PendingIntent? = null
                pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_MUTABLE)
                } else {
                    PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_ONE_SHOT)
                }
                notification = NotificationCompat.Builder(this, "Channel1")

                    .setContentTitle("UMAIA Movesense")
                    .setContentText("Sensor conectado, a recolher dados.")
                    .setSmallIcon(R.mipmap.ic_umaia_logo)
                    .setContentIntent(pendingIntent).build()
                startForeground(1, notification)
            } else {
                val intent1 = Intent(this, ScanDevice::class.java)

                var pendingIntent: PendingIntent? = null
                pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_MUTABLE)
                } else {
                    PendingIntent.getActivity(this, 0, intent1, PendingIntent.FLAG_ONE_SHOT)
                }
                notification = NotificationCompat.Builder(this, "Channel1")

                    .setContentTitle("UMAIA Movesense")
                    .setContentText("Sensor desconectado, por favor conecte-se")
                    .setSmallIcon(R.mipmap.ic_umaia_red_logo)
                    .setContentIntent(pendingIntent).build()
                startForeground(1, notification)
            }
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
//                saveConnectionStatus(true)
                createNotification(true)
                for (sr in bluetoothList) {
                    if (sr.macAddress.equals(macAddress, true)) {
                        sr.markConnected(serial)
                        break
                    }
                }
//                mScanResArrayAdapter.notifyDataSetChanged()

                Toast.makeText(this@MyService, "Conectado.", Toast.LENGTH_SHORT).show()

//                if (Build.VERSION.SDK_INT >= 0) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        startForegroundService(Intent(this@MyService, MyService2::class.java))
//                    }
//                } else {
//                    stopService(Intent(this@MyService, MyService2::class.java))
//                }

//                startService(Intent(this@ScanDevice,MyService::class.java))

                // Open the ECGActivity
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
                        Toast.makeText(this@MyService, "DESCONECTADO2.", Toast.LENGTH_SHORT).show()
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

//    private fun saveConnectionStatus(isConnected : Boolean){
//        Log.d(LOG_TAG, "tou: $isConnected")
//
//        lifecycleScope.launch{
//            dataStore  = DataStoreManager(this@ScanDevice)
//
//            dataStore.setStatus(isConnected)
//
//        }
//
//    }

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