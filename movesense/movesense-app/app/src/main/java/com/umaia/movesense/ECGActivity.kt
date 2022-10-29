package com.umaia.movesense

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.movesense.mds.Mds
import com.movesense.mds.MdsException
import com.movesense.mds.MdsNotificationListener
import com.movesense.mds.MdsSubscription
import com.umaia.movesense.databinding.ActivityEcgactivityBinding
import com.umaia.movesense.responses.HRResponse

class ECGActivity : AppCompatActivity() {

    val SERIAL = "serial"
    var connectedSerial: String? = null
    private var mHRSubscription: MdsSubscription? = null

    val URI_EVENTLISTENER = "suunto://MDS/EventListener"

    val URI_MEAS_HR = "/Meas/HR"

    private lateinit var binding: ActivityEcgactivityBinding

    private val LOG_TAG = ECGActivity::class.java.simpleName
    companion object Foo {
        var s_INSTANCE: ECGActivity? = null

    }

     fun gets_INSTANCE(): ECGActivity? {
        return s_INSTANCE
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEcgactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        s_INSTANCE = this

        // Find serial in opening intent

        connectedSerial = intent.getStringExtra(SERIAL)
        enableHRSubscription()
    }

    private fun enableHRSubscription() {
        // Make sure there is no subscription
        unsubscribeHR()

        // Build JSON doc that describes what resource and device to subscribe
        val sb = StringBuilder()
        val strContract: String =
            sb.append("{\"Uri\": \"").append(connectedSerial).append(URI_MEAS_HR)
                .append("\"}").toString()
        Log.d(LOG_TAG, strContract)
        mHRSubscription = Mds.builder().build(this).subscribe(URI_EVENTLISTENER,
            strContract, object : MdsNotificationListener {
                override fun onNotification(data: String) {
                    Log.d(LOG_TAG, "onNotification(): $data")
                    val hrResponse: HRResponse = Gson().fromJson(
                        data, HRResponse::class.java
                    )
                    if (hrResponse != null) {
                        val hr = hrResponse.body.average as Float
                        (findViewById(R.id.textViewHR) as TextView).text = "" + hr
                        (findViewById(R.id.textViewIBI) as TextView).text =
                            if (hrResponse.body.rrData.isNotEmpty()) "" + hrResponse.body.rrData[hrResponse.body.rrData.size - 1] else "--"
                    }
                }

                override fun onError(error: MdsException) {
                    Log.e(LOG_TAG, "HRSubscription onError(): ", error)
                    unsubscribeHR()
                }
            })
    }
    private fun unsubscribeHR() {
        if (mHRSubscription != null) {
            mHRSubscription!!.unsubscribe()
            mHRSubscription = null
        }
    }

    fun unsubscribeAll() {
        Log.d(LOG_TAG, "unsubscribeAll()")
        // TODO: asdasd
//        unsubscribeECG()
//        unsubscribeHR()
    }
}