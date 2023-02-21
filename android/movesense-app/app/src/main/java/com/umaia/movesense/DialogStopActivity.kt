package com.umaia.movesense

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.umaia.movesense.data.network.NetworkChecker
import com.umaia.movesense.databinding.DialogInternetBinding
import com.umaia.movesense.databinding.DialogLogoutBinding
import com.umaia.movesense.databinding.DialogWifiBinding
import com.umaia.movesense.ui.auth.LoginActivity
import com.umaia.movesense.data.suveys.home.startNewActivity
import com.umaia.movesense.databinding.DialogStopBinding
import com.umaia.movesense.services.MovesenseService
import com.umaia.movesense.util.Constants

class DialogStopActivity(
    var activity: Activity
) : DialogFragment() {
    private lateinit var binding: DialogStopBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogStopBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners(view)

    }
    private fun sendCommandToService(action: String) {
        requireActivity().startService(Intent(context, MovesenseService::class.java).apply {
            this.action = action
        })
    }

    private var listener: DialogInternet.OnDialogInternetismissListener? = null

    fun setOnDialogDismissListener(listener: DialogInternet.OnDialogInternetismissListener) {
        this.listener = listener
    }

    private fun setupClickListeners(view: View) {
        binding.sim.setOnClickListener {
            sendCommandToService(Constants.ACTION_STOP_SERVICE)
        }
        binding.cancelar.setOnClickListener{
            dismiss()
        }
    }

}