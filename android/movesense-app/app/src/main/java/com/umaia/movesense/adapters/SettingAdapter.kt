package com.umaia.movesense.adapters

import android.R
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.umaia.movesense.AuthViewModel
import com.umaia.movesense.DialogLogout
import com.umaia.movesense.GlobalClass
import com.umaia.movesense.SensorSettingsActivity
import com.umaia.movesense.databinding.ItemDefinicoesBinding
import com.umaia.movesense.model.SettingsClass
import com.umaia.movesense.ui.home.startNewActivity
import com.umaia.movesense.util.Constants


class SettingsAdapter : RecyclerView.Adapter<SettingsAdapter.HolderDefinicoes> {

    // context, get using construtor
    private var context: Context
    private var gv = GlobalClass()
    private var settingsList: ArrayList<SettingsClass>
    private var authViewModel: AuthViewModel
    private var activity: Activity

    //viewbinding RowReviewsBinding.xml
    private lateinit var binding: ItemDefinicoesBinding

    // construtor
    constructor(
        context: Context,
        reviewArrayList: ArrayList<SettingsClass>,
        authViewModel: AuthViewModel,
        activity: Activity
    ) {
        this.context = context
        this.settingsList = reviewArrayList
        this.authViewModel = authViewModel
        this.activity = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDefinicoes {
        binding = ItemDefinicoesBinding.inflate(LayoutInflater.from(context), parent, false)
        gv = parent.context.applicationContext as GlobalClass



        return HolderDefinicoes(binding.root)
    }

    override fun onBindViewHolder(holder: HolderDefinicoes, position: Int) {
        val currentItem = settingsList[position]

        currentItem.icon?.let { holder.imagemProduto.setImageResource(it) }
        currentItem.name?.let { holder.nomeSettings.setText(it) }
        currentItem.description?.let { holder.descricao.setText(it) }


//        if (currentItem.imagemSetting != null) {
//            Glide.with(holder.itemView.context).load(currentItem.imagemSetting)
//                .into(holder.imagemProduto)
//        } else {
//            Glide.with(holder.itemView.context).load(R.drawable.ic_camera)
//                .into(holder.imagemProduto)


//        }
//        holder.descricao.text = currentItem.descricao
//        holder.nomeSettings.text = currentItem.nomeSetting

        binding.layout.setOnClickListener {
            if (currentItem.id == Constants.SETTINGS_SENSORS) {

                (context as Activity).startActivity(Intent(context,SensorSettingsActivity::class.java))
                // TODO: Quando clicar no sensores abrir activity com a lista de sensores.

            } else if (currentItem.id == Constants.SETTINGS_LOGOUT) {
                // TODO: DIALOG tem a certeza?
                var dialog = DialogLogout(authViewModel, activity)
                dialog.show((context as FragmentActivity).supportFragmentManager, ContentValues.TAG)

            }
        }
    }

    override fun getItemCount(): Int {
        return settingsList.size
    }


    inner class HolderDefinicoes(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeSettings = binding.nomeSetting
        val imagemProduto = binding.iconeNaLista
        val descricao = binding.descricao
    }
}

