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
import com.umaia.movesense.ui.home.startNewActivity

class DialogInternet(
    var viewmodel: ApiViewModel,
    var activity: Activity
) : DialogFragment() {
    private lateinit var binding: DialogInternetBinding
    private lateinit var networkChecker: NetworkChecker



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogInternetBinding.inflate(inflater, container, false)
        networkChecker = NetworkChecker(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners(view)

    }

    private fun setupClickListeners(view: View) {
        binding.ok.setOnClickListener {
            if(networkChecker.hasInternet()){
                dismiss()
                requireActivity().startNewActivity(MainActivity::class.java)
            }
        }
    }

}