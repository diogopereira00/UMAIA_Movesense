package com.umaia.movesense.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.umaia.movesense.GlobalClass
import com.umaia.movesense.databinding.FragmentHomeBinding
import com.umaia.movesense.model.MoveSenseEvent
import com.umaia.movesense.services.MovesenseService
import com.umaia.movesense.util.Constants
import timber.log.Timber


class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    lateinit var gv: GlobalClass

    companion object Foo {
        var s_INSTANCE: Home? = null

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        gv = activity?.application as GlobalClass

        setObservers()
        s_INSTANCE = this
        updateHR()

        binding.buttonStart.setOnClickListener {
            sendCommandToService(Constants.ACTION_START_SERVICE)

        }
        binding.buttonStop.setOnClickListener{
            sendCommandToService(Constants.ACTION_STOP_SERVICE)

        }


        return binding.root
    }


    private fun updateHR() {
        binding.textViewHR.text = gv.hrAvarage.toString()
        binding.textViewIBI.text = gv.hrRRdata

    }

    private fun setObservers() {
        MovesenseService.moveSenseEvent.observe(viewLifecycleOwner, Observer {
            updateUi(it)
        })
        MovesenseService.movesenseHeartRate.observe(viewLifecycleOwner, Observer {
            updateHR()
        })


    }

    private fun updateUi(event: MoveSenseEvent) {
        when (event) {
            is MoveSenseEvent.START -> {
                switchService(true)
            }
            is MoveSenseEvent.STOP -> {
                switchService(false)
            }
        }
    }

    fun executeOnStatusChanged(switch: CompoundButton, isChecked: Boolean) {
        Timber.e("Status changed")
    }





    fun onStopClicked(view: View?) {
//        switchService(false)
        sendCommandToService(Constants.ACTION_STOP_SERVICE)

    }

    private fun sendCommandToService(action: String) {


        requireActivity().startService(Intent(context, MovesenseService::class.java).apply {
            this.action = action
        })
    }

    private fun switchService(isStarted: Boolean) {
        if (isStarted) {
            binding.buttonStart.visibility = View.GONE
            binding.buttonStop.visibility = View.VISIBLE
            Timber.e("on")

        } else {
            binding.buttonStart.visibility = View.VISIBLE
            binding.buttonStop.visibility = View.GONE
            Timber.e("off")

        }

    }



}