package com.umaia.movesense.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build

class NetworkChecker(private val context: Context)  {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    //Verifica se existe conexão wifi.
    fun hasInternetWifi(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network: Network = connectivityManager?.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(
                NetworkCapabilities.TRANSPORT_VPN
            )
        } else {
            val activeNetworkInfo = connectivityManager?.activeNetworkInfo
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI
            }
            false
        }

    }
    fun hasInternet(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network: Network = connectivityManager?.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        } else {
            val activeNetworkInfo = connectivityManager?.activeNetworkInfo
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI ||  activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE
            }
            false
        }
    }



}