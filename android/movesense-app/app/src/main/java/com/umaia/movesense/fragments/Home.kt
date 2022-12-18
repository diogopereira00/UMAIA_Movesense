package com.umaia.movesense.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.umaia.movesense.ApiViewModel
import com.umaia.movesense.GlobalClass
import com.umaia.movesense.data.AppDataBase
import com.umaia.movesense.data.acc.ACCRepository
import com.umaia.movesense.data.gyro.GYRORepository
import com.umaia.movesense.data.network.ServerApi
import com.umaia.movesense.data.network.RemoteDataSource
import com.umaia.movesense.data.network.Resource
import com.umaia.movesense.data.repository.ApiRepository
import com.umaia.movesense.databinding.FragmentHomeBinding
import com.umaia.movesense.model.MoveSenseEvent
import com.umaia.movesense.model.MovesenseWifi
import com.umaia.movesense.services.MovesenseService
import com.umaia.movesense.ui.home.observeOnce
import com.umaia.movesense.util.Constants
import com.umaia.movesense.util.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber


class Home : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    lateinit var gv: GlobalClass
    private lateinit var accRepository: ACCRepository
    private lateinit var gyroRepository: GYRORepository

    private lateinit var jsonString: String

    private lateinit var viewModel: ApiViewModel
    private val remoteDataSource = RemoteDataSource()

    private var countAcc: Int = 0

    companion object Foo {
        var s_INSTANCE: Home? = null
    }

    override fun onResume() {
        super.onResume()
        Timber.e("Atenção ->>>>>>>>>>>>>>>>>>>> ${gv.getscannerECG()}")
        Timber.e("->>>>>>>>>>>>>>>>>> ${gv.isAccActivated}")

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentHomeBinding.inflate(layoutInflater)
        gv = activity?.application as GlobalClass
        Timber.e("Atenção ->>>>>>>>>>>>>>>>>>>> ${gv.getscannerECG()}")

        val accDao = AppDataBase.getDatabase(requireContext()).accDao()
        accRepository = ACCRepository(accDao)
        val gyroDao = AppDataBase.getDatabase(requireContext()).gyroDao()
        gyroRepository = GYRORepository(gyroDao)


        val factory =
            ViewModelFactory(
                ApiRepository(api = remoteDataSource.buildApi(ServerApi::class.java), null)
            )

        viewModel = ViewModelProvider(this, factory)[ApiViewModel::class.java]





        setObservers()
        s_INSTANCE = this
        updateHR()

        binding.buttonStart.setOnClickListener {
            sendCommandToService(Constants.ACTION_START_SERVICE)
        }
        binding.buttonStop.setOnClickListener {
            sendCommandToService(Constants.ACTION_STOP_SERVICE)
        }

        //TODO adiconar o resto dos sensores, efetuar mais alguns testes, e mudar o nome da resposta.
        binding.buttonTest.setOnClickListener {

//            if (countAcc >= 2) {
                var accTable = accRepository.getAllACC
                accTable.observeOnce(viewLifecycleOwner) {
                    if(it.size>=2) {
                        countAcc = it.size
                        jsonString = Gson().toJson(it)
                        viewModel.addACCData(jsonString = jsonString, authToken = gv.authToken)
                    }
                }
            var gyroTable = accRepository.getAllACC
            gyroTable.observeOnce(viewLifecycleOwner) {
                if(it.size>=2) {
                    countAcc = it.size
                    jsonString = Gson().toJson(it)
                    viewModel.addGyroData(jsonString = jsonString, authToken = gv.authToken)
                }
            }

//            }
            viewModel.uploadDataAccResponses.observeOnce(viewLifecycleOwner) {
                when (it) {
                    is Resource.Success -> {
                        Timber.e(countAcc.toString())
                        lifecycleScope.launch(Dispatchers.IO) {
                            accRepository.deleteAll()

                        }
                        Toast.makeText(context, "Dados adicionados", Toast.LENGTH_LONG).show()

                    }
                    is Resource.Failure -> {
                        Toast.makeText(context, "Erro", Toast.LENGTH_LONG).show()
                    }
                }
            }
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
        MovesenseService.movesenseWifi.observe(viewLifecycleOwner, Observer {
            when (it) {
                is MovesenseWifi.AVAILABLE -> {
                    binding.textviewConnectiviy.text = "Status is available"

                }

                is MovesenseWifi.UNAVAILABLE -> {
                    binding.textviewConnectiviy.text = "Status is unavailable"
                }
            }
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