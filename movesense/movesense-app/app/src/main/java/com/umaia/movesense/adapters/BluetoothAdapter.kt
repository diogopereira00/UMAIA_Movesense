package com.umaia.movesense.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umaia.movesense.MyScanResult
import com.umaia.movesense.databinding.ItemBluethoothListBinding

class BluetoothAdapter : RecyclerView.Adapter<BluetoothAdapter.HolderDefinicoes>  {



    // context, get using construtor
    private var context: Context
    private var bluetoothList: ArrayList<MyScanResult>

    //viewbinding RowReviewsBinding.xml
    private lateinit var binding: ItemBluethoothListBinding

    // construtor
    constructor(context: Context, reviewArrayList: ArrayList<MyScanResult>) {
        this.context = context
        this.bluetoothList = reviewArrayList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDefinicoes {
        binding = ItemBluethoothListBinding.inflate(LayoutInflater.from(context), parent, false)
//        gv = parent.context.applicationContext as GlobalClass
        return HolderDefinicoes(binding.root)
    }

    override fun onBindViewHolder(holder: HolderDefinicoes, position: Int) {
        val currentItem = bluetoothList[position]
//        if (currentItem.imagemSetting != null) {
//            Glide.with(holder.itemView.context).load(currentItem.imagemSetting)
//                .into(holder.imagemProduto)
//        } else {
//            Glide.with(holder.itemView.context).load(R.drawable.ic_camera)
//                .into(holder.imagemProduto)
//
//
//        }
        holder.nomeSettings.text = currentItem.name
        holder.macAddress.text = currentItem.macAddress
        holder.rssi.text ="[" + currentItem.rssi.toString() + "]"
        holder.serial.text = currentItem.serial


//        binding.layout.setOnClickListener {
//            if(currentItem.id=="Logout") {
//                FirebaseAuth.getInstance().signOut()
//                var intent = Intent(context, AuthenticationActivity::class.java)
//                context.startActivity(intent)
//
//            }
//        }
    }

    override fun getItemCount(): Int {
        return bluetoothList.size
    }


    inner class HolderDefinicoes(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nomeSettings = binding.nome
        val serial = binding.serial
        val rssi = binding.rssi
        val macAddress = binding.macaddress
    }
}

