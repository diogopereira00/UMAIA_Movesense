package com.umaia.movesense

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.movesense.mds.MdsSubscription
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import com.umaia.movesense.adapters.BluetoothAdapter
import com.umaia.movesense.databinding.ActivityScanBinding
import com.umaia.movesense.services.MovesenseService
import com.umaia.movesense.util.Constants
import com.umaia.movesense.util.Functions.checkAndRequestPermissions
import com.umaia.movesense.util.Functions.requestNeededPermissions
import io.reactivex.disposables.Disposable

class ScanActivity : AppCompatActivity() {

    lateinit var gv: GlobalClass
    private lateinit var binding: ActivityScanBinding
    private var mdsSubscription: MdsSubscription? = null

    //  UI
    private lateinit var botaoScan: Button
    private lateinit var botaoStop: Button
    private lateinit var imagemNoDeviceFound: ImageView
    private lateinit var textNotFound1: TextView
    private lateinit var textNotFound2: TextView
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mBluetoothRecyclerView: RecyclerView

    private var mBleClient: RxBleClient? = null
    var mBluetoothSubscription: Disposable? = null

    private val buttonUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.getStringExtra("action")
            when (action) {
                "enable" -> {
                    binding.buttonScan.isEnabled = true
                    binding.buttonScan.isClickable = true
                }
                "disable" -> {
                    binding.buttonScan.isEnabled = false
                    binding.buttonScan.isClickable = false
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        gv = application as GlobalClass

        bindUI()
        checkAndRequestPermissions(this)
        //requestNeededPermissions(this)
        mBluetoothAdapter.setOnItemClickListener(object : BluetoothAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                if (gv.bluetoothList.size < 0)
                    return
                binding.recyclerView.isClickable = false
                gv.currentDevice = gv.bluetoothList[position]
                LocalBroadcastManager.getInstance(this@ScanActivity)
                    .registerReceiver(buttonUpdateReceiver, IntentFilter("update_button"))
                //Se não tiver conectado.
                if (!gv.currentDevice.isConnected) {
                    // Stop scanning
//                    binding.buttonScan.isEnabled = false
                    binding.buttonScan.isClickable = false
                    binding.buttonScan.text = "A conectar..."
                    Toast.makeText(
                        this@ScanActivity,
                        "A conectar a ${gv.currentDevice.name} ${gv.currentDevice.serial}",
                        Toast.LENGTH_LONG
                    ).show()
                    onScanStopClicked(null)
                    // And connect to the device

                    sendCommandToService(Constants.ACTION_BLUETOOTH_CONNECTED)


                }
            }
        })
    }

    private fun sendCommandToService(action: String) {
        startService(Intent(this, MovesenseService::class.java).apply {
            this.action = action
        })
    }
    fun onScanClicked(view: View?) {
        botaoScan.visibility = View.GONE
        botaoStop.visibility = View.VISIBLE
        textNotFound2.text = "A procura de equipamentos..."

        // Start with empty list
        gv.bluetoothList.clear()
        mBluetoothAdapter!!.notifyDataSetChanged()
        mBluetoothSubscription = getBleClient()!!.scanBleDevices(
            ScanSettings.Builder()
                // .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY) // change if needed
                // .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES) // change if needed
                .build() // add filters if needed
        )
            .subscribe(
                { scanResult: ScanResult ->
                    Log.d("ScanDevice", "scanResult: $scanResult")

                    // Process scan result here. filter movesense devices.
                    if (scanResult.bleDevice != null && scanResult.bleDevice.name != null &&
                        scanResult.bleDevice.name!!.startsWith("Movesense")
                    ) {

                        // replace if exists already, add otherwise
                        val msr = MyScanResult(scanResult)
                        if (gv.bluetoothList.contains(msr))
                            gv.bluetoothList[gv.bluetoothList.indexOf(msr)] = msr
                        else
                            gv.bluetoothList.add(0, msr)

                        mBluetoothAdapter!!.notifyDataSetChanged()
                        //TODO if bluetooth is off
                        if (gv.bluetoothList.size > 0) {
                            imagemNoDeviceFound.visibility = View.GONE
                            textNotFound1.visibility = View.GONE
                            textNotFound2.visibility = View.GONE
                            mBluetoothRecyclerView.visibility = View.VISIBLE
                        } else {
                            imagemNoDeviceFound.visibility = View.VISIBLE
                            textNotFound1.visibility = View.VISIBLE
                            textNotFound2.visibility = View.VISIBLE
                            mBluetoothRecyclerView.visibility = View.GONE
                        }
                    }
                },
                { throwable: Throwable ->
                    Log.d("Main Activity", "scanResult: $throwable")
                    // Handle an error here.

                    // Re-enable scan buttons, just like with ScanStop
                    onScanStopClicked(null)
                }
            )
    }

    fun onScanStopClicked(view: View?) {
        if (mBluetoothSubscription != null) {
            mBluetoothSubscription!!.dispose()
            mBluetoothSubscription = null
        }
        botaoScan.visibility = View.VISIBLE
        botaoStop.visibility = View.GONE
        if (gv.bluetoothList.size == 0) {
            textNotFound2.text =
                "Verifique se o equipamento está ligado,\n e por favor tente novamente"
        }
    }

    private fun getBleClient(): RxBleClient? {
        // Init RxAndroidBle (Ble helper library) if not yet initialized
        if (mBleClient == null) {
            mBleClient = RxBleClient.create(this)
        }
        return mBleClient
    }


    private fun bindUI() {
        botaoScan = binding.buttonScan
        botaoStop = binding.buttonScanStop
        imagemNoDeviceFound = binding.noDeviceImage
        textNotFound1 = binding.noDevice1
        textNotFound2 = binding.noDevice2

        mBluetoothRecyclerView = binding.recyclerView
        mBluetoothRecyclerView.layoutManager = LinearLayoutManager(this)
        mBluetoothAdapter = BluetoothAdapter(gv.bluetoothList)
        mBluetoothRecyclerView.adapter = mBluetoothAdapter
    }
}