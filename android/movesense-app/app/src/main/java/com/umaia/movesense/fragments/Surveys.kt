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
import com.umaia.movesense.adapters.SurveysAdapter
import com.umaia.movesense.data.network.ServerApi
import com.umaia.movesense.data.network.RemoteDataSource
import com.umaia.movesense.data.suveys.options.repository.ApiRepository
import com.umaia.movesense.data.responses.UserPreferences
import com.umaia.movesense.databinding.FragmentSurveysBinding
import com.umaia.movesense.model.SettingsClass
import com.umaia.movesense.model.SurveysClass
import com.umaia.movesense.util.Constants
import com.umaia.movesense.util.ViewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Surveys : Fragment() {

    private lateinit var binding: FragmentSurveysBinding
    lateinit var gv: GlobalClass

    private  var surveyList: ArrayList<SurveysClass> = ArrayList()
    private lateinit var surveyAdapter: SurveysAdapter
    private lateinit var surveyRecyclerView: RecyclerView
    private lateinit var viewModel: ApiViewModel
    private val remoteDataSource = RemoteDataSource()
    private lateinit var userPreferences: UserPreferences

    companion object Foo {
        var s_INSTANCE: Home? = null

    }

    override fun onResume() {
        super.onResume()
        surveyAdapter.notifyDataSetChanged()
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
        surveyAdapter = SurveysAdapter(requireContext(), surveyList, viewModel,requireActivity())
        surveyRecyclerView = binding.recyclerViewDefinicoes
        surveyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        surveyRecyclerView.adapter = surveyAdapter


        return binding.root
    }

    private fun gerarLista() {
        surveyList.clear()
        surveyList.add(SurveysClass(studyName = "Estudo sobre habitos do sono", surveyName = "Questionario 1", startTime ="2022-12-15 09:00:00", endTime = "2022-12-15 09:05:00", expectedTime = 3))
        surveyList.add(SurveysClass(studyName = "Estudo sobre habitos do sono", surveyName = "Questionario 2", startTime ="2022-12-15 11:00:00", endTime = "2022-12-15 11:40:00", expectedTime = 3))
        surveyList.add(SurveysClass(studyName = "Estudo sobre habitos do sono", surveyName = "Questionario 3", startTime ="2022-12-15 18:00:00", endTime = "2022-12-15 18:05:00", expectedTime = 3))



    }





}