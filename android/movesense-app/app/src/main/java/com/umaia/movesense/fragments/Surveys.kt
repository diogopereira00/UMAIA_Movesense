package com.umaia.movesense.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.umaia.movesense.ApiViewModel
import com.umaia.movesense.GlobalClass
import com.umaia.movesense.R
import com.umaia.movesense.adapters.SettingsAdapter
import com.umaia.movesense.data.network.ServerApi
import com.umaia.movesense.data.network.RemoteDataSource
import com.umaia.movesense.data.repository.ApiRepository
import com.umaia.movesense.data.responses.UserPreferences
import com.umaia.movesense.databinding.FragmentSettingsBinding
import com.umaia.movesense.databinding.FragmentSurveysBinding
import com.umaia.movesense.model.SettingsClass
import com.umaia.movesense.util.Constants
import com.umaia.movesense.util.ViewModelFactory


class Surveys : Fragment() {

    private lateinit var binding: FragmentSurveysBinding
    lateinit var gv: GlobalClass

    private  var settingsList: ArrayList<SettingsClass> = ArrayList()
    private lateinit var definicoesAdapter: SettingsAdapter
    private lateinit var definicoesRecyclerView: RecyclerView
    private lateinit var viewModel: ApiViewModel
    private val remoteDataSource = RemoteDataSource()
    private lateinit var userPreferences: UserPreferences

    companion object Foo {
        var s_INSTANCE: Home? = null

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSurveysBinding.inflate(layoutInflater)
        gv = activity?.application as GlobalClass


        userPreferences = UserPreferences(requireContext())

        val factory =
            ViewModelFactory(
                ApiRepository(
                    remoteDataSource.buildApi(ServerApi::class.java),
                    userPreferences
                )
            )

        viewModel = ViewModelProvider(this, factory)[ApiViewModel::class.java]
//        loadSharedPreferences()
//        initSwitchListener();

        gerarLista()
        definicoesAdapter = SettingsAdapter(requireContext(), settingsList, viewModel,requireActivity())
        definicoesRecyclerView = binding.recyclerViewDefinicoes
        definicoesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        definicoesRecyclerView.adapter = definicoesAdapter


        return binding.root
    }

    private fun gerarLista() {
        settingsList.clear()
        settingsList.add(SettingsClass(id = "Conta", name = "Sua conta", description = "Veja a segurança da sua conta, baixe um arquivo com seus dados ou saiba mais sobre as opções de desativação da conta", icon = R.drawable.ic_baseline_account_circle_24))
        settingsList.add(SettingsClass(id = Constants.SETTINGS_SENSORS, name = "Os seus sensores", description = "Ative ou desative a recolha de determinados dados, ou altere a frequência da sua recolha.", icon = R.drawable.ic_baseline_sensors_24))

//        settingsList.add(SettingsClass(id = "Segurança",name = "Segurança e acesso à conta", description = "Gira a segurança da sua conta e monitorize o uso dela, inclusive os aplicativos conectados.", icon = R.drawable.ic_baseline_lock_24))
//        settingsList.add(SettingsClass(id = "Privacidade",name = "Privacidade e segurança", description = "Consulte as informações que vê e partilha no RackIT", icon = R.drawable.ic_baseline_security_24))
//        settingsList.add(SettingsClass(id = "Notificacoes",name = "Notificações", description = "Selecione os tipos de informações que recebe sobre atividades, interesses e recomendações", icon = R.drawable.ic_baseline_notifications_24))
//        settingsList.add(SettingsClass(id = "Acessibilidade",name ="Acessibilidade, exibição e idiomas", description = "Selecione a forma como o conteudo é exibido", icon = R.drawable.ic_baseline_remove_red_eye_24))
//        settingsList.add(SettingsClass(id = "Recursos adicionais",name = "Recursos adicionais", description = "Verifique informações uteis do RackIT", icon = R.drawable.ic_baseline_more_horiz_24))
        settingsList.add(SettingsClass(id = Constants.SETTINGS_LOGOUT,name = "Terminar sessão", description = "Termine sessão em segurança", icon = R.drawable.ic_baseline_logout_24))



    }





}