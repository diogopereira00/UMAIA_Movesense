package com.umaia.movesense

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.umaia.movesense.databinding.ActivityHomeBinding
import com.umaia.movesense.model.MoveSenseEvent
import com.umaia.movesense.services.MovesenseService
import com.umaia.movesense.util.Constants
import timber.log.Timber


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private var isServiceRunning = false
    lateinit var gv : GlobalClass

    companion object Foo {
        var s_INSTANCE: HomeActivity? = null

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        gv = application as GlobalClass

        setObservers()
        s_INSTANCE=this
        updateHR()
    }

    private fun updateHR() {
        binding.textViewHR.text = gv.hrAvarage.toString()
        binding.textViewIBI.text = gv.hrRRdata

    }

    private fun setObservers() {
        MovesenseService.moveSenseEvent.observe(this, Observer {
            updateUi(it)
        })
        MovesenseService.movesenseHeartRate.observe(this,Observer{
            updateHR()
        })


    }
    private fun updateUi(event: MoveSenseEvent) {
        when (event) {
            is MoveSenseEvent.START -> {
                isServiceRunning = true
                binding.buttonStart.visibility=View.GONE
                binding.buttonStop.visibility=View.VISIBLE
                Timber.e("on")
            }
            is MoveSenseEvent.STOP -> {
                isServiceRunning = false
                binding.buttonStart.visibility=View.VISIBLE
                binding.buttonStop.visibility=View.GONE
                Timber.e("off")
            }
        }
    }

    fun executeOnStatusChanged(switch: CompoundButton, isChecked: Boolean) {
        Timber.e("Status changed")
    }

    fun onStartClicked(view: View?) {
//        switchService(true)
        sendCommandToService(Constants.ACTION_START_SERVICE)

    }



    fun onStopClicked(view: View?) {
//        switchService(false)
        sendCommandToService(Constants.ACTION_STOP_SERVICE)

    }

    private fun sendCommandToService(action: String) {
        startService(Intent(this, MovesenseService::class.java).apply {
            this.action = action
        })
    }

    private fun switchService(isStarted : Boolean){
        if(isStarted){
            binding.buttonStart.visibility=View.GONE
            binding.buttonStop.visibility=View.VISIBLE
            Timber.e("on")

        }
        else{
            binding.buttonStart.visibility=View.VISIBLE
            binding.buttonStop.visibility=View.GONE
            Timber.e("off")

        }

    }


}