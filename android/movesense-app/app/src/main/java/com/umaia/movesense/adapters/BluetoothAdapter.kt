package com.umaia.movesense.adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umaia.movesense.MyScanResult
import com.umaia.movesense.databinding.ItemBluethoothListBinding
import java.io.Serializable

class BluetoothAdapter // construtor
    (reviewArrayList: ArrayList<MyScanResult>) :
    RecyclerView.Adapter<BluetoothAdapter.HolderDefinicoes>(), Serializable {


    // context, get using construtor
    private var bluetoothList: ArrayList<MyScanResult> = reviewArrayList

    //viewbinding RowReviewsBinding.xml
    private lateinit var binding: ItemBluethoothListBinding

    //interface do listener
    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {

        fun onItemClick(position: Int)

    }

    fun setOnItemClickListener(listener: onItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDefinicoes {
        binding =
            ItemBluethoothListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        gv = parent.context.applicationContext as GlobalClass
        return HolderDefinicoes(binding.root, mListener)
    }


    override fun onBindViewHolder(holder: HolderDefinicoes, position: Int) {
        val currentItem = bluetoothList[position]

        if (currentItem.isConnected)
            holder.nomeSettings.setTypeface(null, Typeface.BOLD)
        else
            holder.nomeSettings.setTypeface(null, Typeface.NORMAL)

        holder.nomeSettings.text = "UMAIA ${currentItem.name}"
        holder.macAddress.text = currentItem.macAddress
        holder.rssi.text = "[" + currentItem.rssi.toString() + "]"
        holder.serial.text = currentItem.serial

    }

    override fun getItemCount(): Int {
        return bluetoothList.size
    }


    inner class HolderDefinicoes(itemView: View, listener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {
        val nomeSettings = binding.nome
        val serial = binding.serial
        val rssi = binding.rssi
        val macAddress = binding.macaddress

        //funcçao do listener
        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
                itemView.isClickable = false
            }
        }
    }
}

