package com.umaia.movesense

import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.scan.ScanResult

class MyScanResult(scanResult: ScanResult) {
    var rssi: Int
    var macAddress: String
    var name: String?
    var connectedSerial: String? = null
    var serial : String
    init {
        val splitName = scanResult.bleDevice.name!!.split(" ")
        macAddress = scanResult.bleDevice.macAddress
        rssi = scanResult.rssi
        name = splitName[0]
        serial = splitName[1]
    }

    val isConnected: Boolean
        get() = connectedSerial != null

    fun markConnected(serial: String?) {
        connectedSerial = serial
    }

    fun markDisconnected() {
        connectedSerial = null
    }

    override fun equals(`object`: Any?): Boolean {
        return if (`object` is MyScanResult && `object`.macAddress == macAddress) {
            true
        } else if (`object` is RxBleDevice && `object`.macAddress == macAddress) {
            true
        } else {
            false
        }
    }

    override fun toString(): String {
        return (if (isConnected) "Concectado:  " else "") + name + " - " + macAddress + " [" + rssi + "]" + if (isConnected) "" else ""
    }
}