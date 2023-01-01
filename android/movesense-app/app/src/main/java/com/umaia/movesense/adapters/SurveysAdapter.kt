package com.umaia.movesense.adapters

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.rgb
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.umaia.movesense.ApiViewModel
import com.umaia.movesense.DialogLogout
import com.umaia.movesense.GlobalClass
import com.umaia.movesense.SensorSettingsActivity
import com.umaia.movesense.data.suveys.organization.Organization
import com.umaia.movesense.databinding.ItemSurveyListBinding
import com.umaia.movesense.model.SurveysClass
import com.umaia.movesense.util.Constants
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.min


class SurveysAdapter : RecyclerView.Adapter<SurveysAdapter.HolderDefinicoes> {

    // context, get using construtor
    private var context: Context
    private var gv = GlobalClass()
    private var surveysList: ArrayList<SurveysClass>
    private var authViewModel: ApiViewModel
    private var activity: Activity

    //viewbinding RowReviewsBinding.xml
    private lateinit var binding: ItemSurveyListBinding

    // construtor
    constructor(
        context: Context,
        surveyList: ArrayList<SurveysClass>,
        authViewModel: ApiViewModel,
        activity: Activity
    ) {
        this.context = context
        this.surveysList = surveyList
        this.authViewModel = authViewModel
        this.activity = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDefinicoes {
        binding = ItemSurveyListBinding.inflate(LayoutInflater.from(context), parent, false)
        gv = parent.context.applicationContext as GlobalClass



        return HolderDefinicoes(binding.root)
    }

    override fun onBindViewHolder(holder: HolderDefinicoes, position: Int) {
        val currentItem = surveysList[position]

        currentItem.surveyName?.let {
            holder.surveyName.text = it
        }
        currentItem.studyName?.let {
            holder.studyName.text = it
        }

        val sdf = SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss")
        val startTime = sdf.parse(currentItem.startTime!!)
        val endTime = sdf.parse(currentItem.endTime!!)

        val currentTime = sdf.parse(sdf.format(Date()))

        if (currentTime.after(startTime) && currentTime.before(endTime)) {
            //Se o current time estiver entre o start time e o endTime
            holder.runningStatus.text = "Aberto"
            holder.runningStatus.setTextColor(
                rgb(0, 153, 0)
            )
        } else if (currentTime.before(startTime)) {

            holder.runningStatus.setTextColor(
                rgb(255, 170, 0)
            )

            val currentTimeCalendar = Calendar.getInstance() // pega o tempo atual
            val startTimeCalendar =
                Calendar.getInstance() // cria um novo calendário para o tempo de início
            startTimeCalendar.set(
                startTime.year,
                startTime.month,
                startTime.date,
                startTime.hours,
                startTime.minutes
            ) // define o tempo de início
            currentTimeCalendar.set(
                currentTime.year,
                currentTime.month,
                currentTime.date,
                currentTime.hours,
                currentTime.minutes
            ) // define o tempo de início

            // calcula a diferença em milissegundos
            val durationInMillis = startTimeCalendar.timeInMillis - currentTimeCalendar.timeInMillis
            // calcula o tempo restante
            val days = durationInMillis / (24 * 60 * 60 * 1000)
            val hours = durationInMillis / (60 * 60 * 1000) % 24
            val minutes = durationInMillis / (60 * 1000) % 60
            var minutesToString = minutes.toString()

            if(minutes.toString().length<2)
            {
                minutesToString = "0$minutes"
            }

            // verifica o tamanho da diferença e apresenta o resultado no formato adequado
            if (days > 1) {
                holder.runningStatus.text = "Abre daqui a $days dias"

            } else if (days == 1L) {
                holder.runningStatus.text = "Abre daqui a $hours dia"
            } else if (hours >= 1) {
                if (minutes > 1) {
                    holder.runningStatus.text = "Abre daqui a ${hours}h${minutesToString}m"
                } else {
                    holder.runningStatus.text = "Abre daqui a ${hours}h"
                }
            } else {
                holder.runningStatus.text = "Abre daqui a  $minutesToString minutos."
            }
        } else {
            holder.cardView.isClickable = false
            holder.cardView.isFocusable = false
            holder.runningStatus.text = "Fechado"
            holder.runningStatus.setTextColor(rgb(153, 0, 0))
        }


        currentItem.expectedTime?.let {
            holder.expectedTime.text = "$it min "
        }


//        if (currentItem.imagemSetting != null) {
//            Glide.with(holder.itemView.context).load(currentItem.imagemSetting)
//                .into(holder.imagemProduto)
//        } else {
//            Glide.with(holder.itemView.context).load(R.drawable.ic_camera)
//                .into(holder.imagemProduto)


//        }
//        holder.descricao.text = currentItem.descricao
//        holder.nomeSettings.text = currentItem.nomeSetting

        binding.cardView.setOnClickListener {
            if (currentItem.id == Constants.SETTINGS_SENSORS) {

                (context as Activity).startActivity(
                    Intent(
                        context,
                        SensorSettingsActivity::class.java
                    )
                )

            } else if (currentItem.id == Constants.SETTINGS_LOGOUT) {
                var dialog = DialogLogout(authViewModel, activity)
                dialog.show((context as FragmentActivity).supportFragmentManager, ContentValues.TAG)

            }
        }
    }

    override fun getItemCount(): Int {
        return surveysList.size
    }


    inner class HolderDefinicoes(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView = binding.cardView
        val surveyName = binding.surveyName
        val studyName = binding.studyName
        val runningStatus = binding.running
        val expectedTime = binding.expectedTime
        val organizationLogo = binding.ivImagem
    }
}

