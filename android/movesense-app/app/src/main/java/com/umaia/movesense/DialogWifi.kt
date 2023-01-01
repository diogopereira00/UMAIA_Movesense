package com.umaia.movesense

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.umaia.movesense.data.network.NetworkChecker
import com.umaia.movesense.databinding.DialogLogoutBinding
import com.umaia.movesense.databinding.DialogWifiBinding
import com.umaia.movesense.ui.auth.LoginActivity
import com.umaia.movesense.ui.home.startNewActivity

class DialogWifi(
    var viewmodel: ApiViewModel,
    var activity: Activity,

) : DialogFragment() {
    private lateinit var binding: DialogWifiBinding
    private lateinit var networkChecker: NetworkChecker
    lateinit var gv: GlobalClass

    //É um reciever que esta a espera que o estado do wifi mude.
    private val connectivityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if (networkInfo != null && networkInfo.type == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected) {
                // WiFi is connected
                dismiss() // Dismiss the dialog
                activity.startNewActivity(MainActivity::class.java)
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context?.registerReceiver(connectivityReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        context?.unregisterReceiver(connectivityReceiver)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogWifiBinding.inflate(inflater, container, false)
        gv = activity.application as GlobalClass
        if(gv.foundNewStudyVersion){
            binding.sure.text ="Foram encontradas atualizações. Atenção. É recomendado ligar o wifi para efetuar a transferencia dos questionarios evitar custos associados.\nPretende prosseguir como?"
        }
        networkChecker = NetworkChecker(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners(view)

    }

    private fun setupClickListeners(view: View) {
        binding.ok.setOnClickListener {
            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
            startActivity(intent)
        }
        binding.redesMoveis.setOnClickListener {
            gv.useMobileDataThisTime  = true
            dismiss()
            activity.startNewActivity(MainActivity::class.java)
        }
    }
}