package com.umaia.movesense.adapters

import android.app.Activity
import android.content.Context
import android.graphics.text.LineBreaker
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.umaia.movesense.*
import com.umaia.movesense.databinding.ItemSensorSettingsBinding
import com.umaia.movesense.model.SensorSettingsClass
import com.umaia.movesense.ui.home.visible


class SensorSettingsAdapter : RecyclerView.Adapter<SensorSettingsAdapter.HolderDefinicoes> {

    // context, get using construtor
    private var context: Context
    private var gv = GlobalClass()
    private var settingsList: ArrayList<SensorSettingsClass>

    //    private var authViewModel: AuthViewModel
    private var activity: Activity
    private var viewModel: MainViewModel

    //viewbinding RowReviewsBinding.xml
    private lateinit var binding: ItemSensorSettingsBinding

    // construtor
    constructor(
        context: Context,
        settingsList: ArrayList<SensorSettingsClass>,
//        authViewModel: AuthViewModel,
        activity: Activity,
        viewModel: MainViewModel
    ) {
        this.context = context
        this.settingsList = settingsList
//        this.authViewModel = authViewModel
        this.activity = activity
        this.viewModel = viewModel
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDefinicoes {
        binding = ItemSensorSettingsBinding.inflate(LayoutInflater.from(context), parent, false)
        gv = parent.context.applicationContext as GlobalClass



        return HolderDefinicoes(binding.root)
    }


    override fun onBindViewHolder(holder: HolderDefinicoes, position: Int) {
        val currentItem = settingsList[position]

        checkIfIsActivated(currentItem, holder)
        hideDropdown(currentItem,holder)


//        holder.frequency.inputType = 0

        currentItem.name?.let { holder.nome.setText(it) }
        currentItem.description?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                holder.description.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            }


            holder.description.setText(it)
        }
//        currentItem.frequency?.let {
//            holder.frequency.setAdapter(arrayAdapter)
//            holder.frequency.inputType = 0
//
//        }
        currentItem.isActive?.let { holder.switchIsActive.setChecked(it) }


//        if (currentItem.imagemSetting != null) {
//            Glide.with(holder.itemView.context).load(currentItem.imagemSetting)
//                .into(holder.imagemProduto)
//        } else {
//            Glide.with(holder.itemView.context).load(R.drawable.ic_camera)
//                .into(holder.imagemProduto)


//        }
//        holder.descricao.text = currentItem.descricao
//        holder.nomeSettings.text = currentItem.nomeSetting

    }

    private fun hideDropdown(currentItem: SensorSettingsClass, holder: SensorSettingsAdapter.HolderDefinicoes) {
        //TODO trocar para as frequencias
        val tamanhos = activity.resources.getStringArray(R.array.Tamanho)
        if (currentItem.id == "HR" || currentItem.id == "Temperatura") {
            holder.frequency.visible(false)
            holder.box.visible(false)
        } else {
            val arrayAdapter = ArrayAdapter(context, R.layout.dropdown_item, tamanhos)
            holder.frequency.setAdapter(arrayAdapter)
        }
    }

    private fun checkIfIsActivated(currentItem: SensorSettingsClass, holder: HolderDefinicoes) {
        when (currentItem.id) {
            "Acelerometro" -> {
                viewModel.getAccStatus.observe((context as LifecycleOwner)) { isActivated ->
                    if (isActivated != null) {
                        holder.switchIsActive.isChecked = isActivated
                    } else {
                        holder.switchIsActive.isChecked = false
                    }
                }

                holder.switchIsActive.setOnCheckedChangeListener { _, isChecked ->
                    when (isChecked) {
                        true -> {
                            viewModel.setAccStatus(true)
                        }
                        false -> {
                            viewModel.setAccStatus(false)
                        }
                    }
                }
            }
            "Giroscopio" -> {
                viewModel.getGyroStatus.observe((context as LifecycleOwner)) { isActivated ->
                    if (isActivated != null) {
                        holder.switchIsActive.isChecked = isActivated
                    } else {
                        holder.switchIsActive.isChecked = false
                    }
                }

                holder.switchIsActive.setOnCheckedChangeListener { _, isChecked ->
                    when (isChecked) {
                        true -> {
                            viewModel.setGyroStatus(true)
                        }
                        false -> {
                            viewModel.setGyroStatus(false)
                        }
                    }
                }
            }
            "Magnetometro" -> {
                viewModel.getMagnStatus.observe((context as LifecycleOwner)) { isActivated ->
                    if (isActivated != null) {
                        holder.switchIsActive.isChecked = isActivated
                    } else {
                        holder.switchIsActive.isChecked = false
                    }

                }

                holder.switchIsActive.setOnCheckedChangeListener { _, isChecked ->
                    when (isChecked) {
                        true -> {
                            viewModel.setMagnStatus(true)
                        }
                        false -> {
                            viewModel.setMagnStatus(false)
                        }
                    }
                }
            }
            "ECG" -> {
                viewModel.getECGStatus.observe((context as LifecycleOwner)) { isActivated ->
                    if (isActivated != null) {
                        holder.switchIsActive.isChecked = isActivated
                    } else {
                        holder.switchIsActive.isChecked = false
                    }

                }

                holder.switchIsActive.setOnCheckedChangeListener { _, isChecked ->
                    when (isChecked) {
                        true -> {
                            viewModel.setECGStatus(true)
                        }
                        false -> {
                            viewModel.setECGStatus(false)
                        }
                    }
                }
            }
            "HR" -> {
                viewModel.getHRStatus.observe((context as LifecycleOwner)) { isActivated ->
                    if (isActivated != null) {
                        holder.switchIsActive.isChecked = isActivated
                    } else {
                        holder.switchIsActive.isChecked = false
                    }

                }

                holder.switchIsActive.setOnCheckedChangeListener { _, isChecked ->
                    when (isChecked) {
                        true -> {
                            viewModel.setHRStatus(true)
                        }
                        false -> {
                            viewModel.setHRStatus(false)
                        }
                    }
                }
            }
            "Temperatura" -> {
                viewModel.getTempStatus.observe((context as LifecycleOwner)) { isActivated ->
                    if (isActivated != null) {
                        holder.switchIsActive.isChecked = isActivated
                    } else {
                        holder.switchIsActive.isChecked = false
                    }

                }

                holder.switchIsActive.setOnCheckedChangeListener { _, isChecked ->
                    when (isChecked) {
                        true -> {
                            viewModel.setTempStatus(true)
                        }
                        false -> {
                            viewModel.setTempStatus(false)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return settingsList.size
    }


    inner class HolderDefinicoes(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome = binding.nomeSetting
        val description = binding.descricao
        val frequency = binding.AutoCompleteText
        var box = binding.textInputLayout
        val switchIsActive = binding.switch1
    }
}

