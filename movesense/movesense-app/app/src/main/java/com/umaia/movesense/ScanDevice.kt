package com.umaia.movesense

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PackageManagerCompat.LOG_TAG
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.movesense.mds.*
import com.polidea.rxandroidble2.LogConstants
import com.polidea.rxandroidble2.LogOptions
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import com.umaia.movesense.adapters.BluetoothAdapter
import com.umaia.movesense.databinding.ActivityScanDeviceBinding
import com.umaia.movesense.datastore.DataStoreManager
import com.umaia.movesense.datastore.DataStoreViewModel
import com.umaia.movesense.services.MyService
import com.umaia.movesense.services.MyService2
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.launch

class ScanDevice : AppCompatActivity() {


    private val LOG_TAG = MainActivity::class.java.simpleName

    private val MY_PERMISSIONS_REQUEST_LOCATION = 1
    private var mMds: Mds? = null

    val URI_CONNECTEDDEVICES = "suunto://MDS/ConnectedDevices"
    val URI_EVENTLISTENER = "suunto://MDS/EventListener"
    val SCHEME_PREFIX = "suunto://"

    private lateinit var binding: ActivityScanDeviceBinding
    private var bluetoothList: ArrayList<MyScanResult> = ArrayList<MyScanResult>()
    private lateinit var mScanResArrayAdapter: BluetoothAdapter
    private lateinit var mScanResultRecyclerView : RecyclerView


    //NoDeviceFound
    private lateinit var imagemNoDeviceFound : ImageView
    private lateinit var textNotFound1 : TextView
    private lateinit var textNotFound2 : TextView
    lateinit var rxBleClient: RxBleClient

    private var mBleClient: RxBleClient? = null

    // Sensor subscription
    private val URI_MEAS_HR = "Meas/HR"
    private val URI_MEAS_ACC_13 = "/Meas/Acc/13"
    private var mdsSubscription: MdsSubscription? = null
    private var subscribedDeviceSerial: String? = null

    private lateinit var botaoScan : Button
    private lateinit var botaoStop: Button


    private lateinit var dataStore : DataStoreManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanDeviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Binding UI
        botaoScan  = binding.buttonScan
        botaoStop  = binding.buttonScanStop
        imagemNoDeviceFound = binding.noDeviceImage
        textNotFound1 = binding.noDevice1
        textNotFound2 = binding.noDevice2


//        rxBleClient = RxBleClient.create(this)
//        RxBleClient.updateLogOptions(
//            LogOptions.Builder()
//                .setLogLevel(LogConstants.INFO)
//                .setMacAddressLogSetting(LogConstants.MAC_ADDRESS_FULL)
//                .setUuidsLogSetting(LogConstants.UUIDS_FULL)
//                .setShouldLogAttributeValues(true)
//                .build()
//        )

//        dataStore = DataStoreManager(this@ScanDevice)

        mScanResultRecyclerView = binding.recyclerView
        mScanResultRecyclerView.layoutManager = LinearLayoutManager(this)

        mScanResArrayAdapter = BluetoothAdapter(bluetoothList)
        mScanResultRecyclerView.adapter = mScanResArrayAdapter


        //listener scanner, recebe a posiçao do adapter e conecta ao device.
        mScanResArrayAdapter.setOnItemClickListener(object : BluetoothAdapter.onItemClickListener{
            // TODO: ON LONG CLICK
            override fun onItemClick(position: Int) {
                if(bluetoothList.size<0)
                    return
                val device = bluetoothList[position]


                if (!device.isConnected) {
              // Stop scanning
                    Toast.makeText(this@ScanDevice,"A conectar a ${device.name} ${device.serial}", Toast.LENGTH_LONG).show()
                    onScanStopClicked(null)
               // And connect to the device


                    if(Build.VERSION.SDK_INT >=0){
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            var intent = Intent(this@ScanDevice,MyService::class.java)
                            intent.putExtra("device",device)
                            intent.putExtra("bluetoothList",bluetoothList )

                            startForegroundService(intent)
                        }
                    }
                    else{
                        stopService(Intent(this@ScanDevice,MyService::class.java))
                    }

//                    connectBLEDevice(device)
                }



//                else {
//                // Device is connected, trigger showing /Info
//                    subscribeToSensor(device.connectedSerial.toString())
//                }
            }

        })

        requestNeededPermissions()
//        initMds()
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


    private fun unsubscribe() {
        if (mdsSubscription != null) {
            mdsSubscription!!.unsubscribe()
            mdsSubscription = null
        }
        subscribedDeviceSerial = null

        // If UI not invisible, do it now
        val sensorUI: View = findViewById(R.id.sensorUI)
        if (sensorUI.visibility != View.GONE) sensorUI.visibility = View.GONE
    }

    var mScanSubscription: Disposable? = null

    fun onScanClicked(view: View?) {
        botaoScan.visibility =View.GONE
        botaoStop.visibility = View.VISIBLE
        textNotFound2.text = "A procura de equipamentos..."

        // Start with empty list
        bluetoothList.clear()
        mScanResArrayAdapter!!.notifyDataSetChanged()
        mScanSubscription = getBleClient()!!.scanBleDevices(
            ScanSettings.Builder()
                // .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // change if needed
                // .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES) // change if needed
                .build() // add filters if needed
        )
            .subscribe(
                { scanResult: ScanResult ->
                    Log.d( "ScanDevice", "scanResult: $scanResult")

                    // Process scan result here. filter movesense devices.
                    if (scanResult.bleDevice != null && scanResult.bleDevice.name != null &&
                        scanResult.bleDevice.name!!.startsWith("Movesense")
                    ) {

                        // replace if exists already, add otherwise
                        val msr = MyScanResult(scanResult)
                        if (bluetoothList.contains(msr)) bluetoothList[bluetoothList.indexOf(
                            msr
                        )] = msr else bluetoothList.add(0, msr)
                        mScanResArrayAdapter!!.notifyDataSetChanged()
                        //TODO if bluetooth is off
                        if(bluetoothList.size > 0){
                            imagemNoDeviceFound.visibility = View.GONE
                            textNotFound1.visibility = View.GONE
                            textNotFound2.visibility = View.GONE
                            mScanResultRecyclerView.visibility = View.VISIBLE
                        }
                        else{
                            imagemNoDeviceFound.visibility = View.VISIBLE
                            textNotFound1.visibility = View.VISIBLE
                            textNotFound2.visibility = View.VISIBLE
                            mScanResultRecyclerView.visibility = View.GONE
                        }
                    }
                },
                { throwable: Throwable ->
                    Log.d( "Main Activity", "scanResult: $throwable")
                    // Handle an error here.

                    // Re-enable scan buttons, just like with ScanStop
                    onScanStopClicked(null)
                }
            )
    }

    fun requestNeededPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_SCAN
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
    }

    fun onScanStopClicked(view: View?) {
        if (mScanSubscription != null) {
            mScanSubscription!!.dispose()
            mScanSubscription = null
        }
        botaoScan.visibility = View.VISIBLE
        botaoStop.visibility= View.GONE
        if(bluetoothList.size==0){
            textNotFound2.text = "Verifique se o equipamento está ligado,\n e por favor tente novamente"
        }
    }
    fun showDeviceInfo(serial: String?) {
        val uri: String = SCHEME_PREFIX + serial + "/Info"
        val ctx: Context = this
        mMds!![uri, null, object : MdsResponseListener {
            override fun onSuccess(s: String) {
                Log.i(LOG_TAG, "Device $serial /info request succesful: $s")
                // Display info in alert dialog
                val builder: AlertDialog.Builder = AlertDialog.Builder(ctx)
                builder.setTitle("Device info:")
                    .setMessage(s)
                    .show()
            }

            override fun onError(e: MdsException) {
                Log.e(LOG_TAG, "Device $serial /info returned error: $e")
            }
        }]
    }


//    private fun connectBLEDevice(device: MyScanResult) {
//        val bleDevice = getBleClient()!!.getBleDevice(device.macAddress)
//        val me: Activity = this
//        Log.i(LOG_TAG, "Connecting to BLE device: " + bleDevice.macAddress)
//        mMds!!.connect(bleDevice.macAddress, object : MdsConnectionListener {
//            override fun onConnect(s: String) {
//                Log.d(LOG_TAG, "onConnect:$s")
//            }
//
//            override fun onConnectionComplete(macAddress: String, serial: String) {
//                saveConnectionStatus(true)
//                for (sr in bluetoothList) {
//                    if (sr.macAddress.equals(macAddress,true)) {
//                        sr.markConnected(serial)
//                        break
//                    }
//                }
//                mScanResArrayAdapter.notifyDataSetChanged()
//
//                Toast.makeText(this@ScanDevice,"Conectado.", Toast.LENGTH_SHORT).show()
//
//                if(Build.VERSION.SDK_INT >=0){
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                        startForegroundService(Intent(this@ScanDevice,MyService2::class.java))
//                    }
//                }
//                else{
//                    stopService(Intent(this@ScanDevice,MyService2::class.java))
//                }
//
////                startService(Intent(this@ScanDevice,MyService::class.java))
//
//                // Open the ECGActivity
//                val intent = Intent(me, ECGActivity::class.java)
//                intent.putExtra(ECGActivity().SERIAL, serial)
//                startActivity(intent)
//            }
//
//            override fun onError(e: MdsException) {
//                Log.e(LOG_TAG, "onError:$e")
//                showConnectionError(e)
//            }
//
//            override fun onDisconnect(bleAddress: String) {
//
//                Log.d(LOG_TAG, "onDisconnect: $bleAddress")
//
//                Toast.makeText(this@ScanDevice,"DESCONECTADO.", Toast.LENGTH_SHORT).show()
//                if(Build.VERSION.SDK_INT >=0){
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
//                        startForegroundService(Intent(this@ScanDevice,MyService2::class.java))
//                    }
//                }
//                else{
//                    stopService(Intent(this@ScanDevice,MyService2::class.java))
//                }
//
//                for (sr in bluetoothList) {
//                    if (bleAddress == sr.macAddress) {
//                        Toast.makeText(this@ScanDevice,"DESCONECTADO2.", Toast.LENGTH_SHORT).show()
//                        saveConnectionStatus(false)
//
//                        // Unsubscribe all from possible
//                        if (sr.connectedSerial != null && ECGActivity.s_INSTANCE != null &&
//                            sr.connectedSerial.equals(ECGActivity.s_INSTANCE!!.connectedSerial)
//                        ) {
//                            ECGActivity.s_INSTANCE!!.unsubscribeAll()
//                            ECGActivity.s_INSTANCE!!.finish()
////                            stopService(Intent(this@ScanDevice,MyService::class.java))
//                        }
//                        sr.markDisconnected()
//                    }
//                }
//                mScanResArrayAdapter.notifyDataSetChanged()
//            }
//        })
//    }


    private fun saveConnectionStatus(isConnected : Boolean){
        Log.d(LOG_TAG, "tou: $isConnected")

        lifecycleScope.launch{
            dataStore  = DataStoreManager(this@ScanDevice)

            dataStore.setStatus(isConnected)

        }

    }


    private fun showConnectionError(e: MdsException) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            .setTitle("Connection Error:")
            .setMessage(e.message)
        builder.create().show()
    }
}