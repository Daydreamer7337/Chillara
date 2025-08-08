package com.uselessprojects.chillara

import android.bluetooth.BluetoothDevice

data class Results(
    val name: String,
    val amount: String,
    val device: BluetoothDevice
)