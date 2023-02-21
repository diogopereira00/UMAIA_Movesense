package com.umaia.movesense

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.umaia.movesense.data.network.NetworkChecker
import com.umaia.movesense.data.suveys.StudiesViewmodel
import com.umaia.movesense.databinding.DialogAdminPinBinding
import com.umaia.movesense.databinding.DialogLogoutBinding
import com.umaia.movesense.ui.auth.LoginActivity
import com.umaia.movesense.data.suveys.home.startNewActivity
import com.umaia.movesense.util.ViewModelFactory

class AdminPinDialog(
    var viewmodel: ApiViewModel,
    var activity: Activity,

    ) : DialogFragment() {
    private lateinit var binding: DialogAdminPinBinding
    private lateinit var networkChecker: NetworkChecker
    lateinit var gv: GlobalClass
    private lateinit var viewModelStudies: StudiesViewmodel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogAdminPinBinding.inflate(inflater, container, false)
        gv = activity.application as GlobalClass
        val factoryStudies = ViewModelFactory(context = requireContext(), repository = null)
        viewModelStudies = ViewModelProvider(this, factoryStudies)[StudiesViewmodel::class.java]


        binding.submitButton.setOnClickListener {
            val password = binding.editTextPassword.text.toString()
            val checkForAdminPassword = viewModelStudies.getStudyAdminPassword(studyID = "3")
            checkForAdminPassword.observe(viewLifecycleOwner, Observer { studyAdminPassword ->
                if (studyAdminPassword == password) {
                    dismiss()

                    (context as Activity).startActivity(
                        Intent(
                            context,
                            SensorSettingsActivity::class.java
                        )
                    )
                } else {
                    Toast.makeText(context, "Ups, parece que não tens acesso", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            })
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}