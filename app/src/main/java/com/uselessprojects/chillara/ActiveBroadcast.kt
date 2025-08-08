package com.uselessprojects.chillara

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.bluetooth.BluetoothManager
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.ParcelUuid
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.UUID
import android.bluetooth.le.AdvertiseCallback
import androidx.annotation.RequiresPermission

class ActiveBroadcast: AppCompatActivity()
{
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    @RequiresPermission(Manifest.permission.BLUETOOTH_ADVERTISE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.active_broadcast_page)

        bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        val send_msg_btn: Button = findViewById(R.id.send_msg_btn)
        val msg_text_et: EditText = findViewById(R.id.msg_text_et)

        val messages_tv: TextView = findViewById(R.id.messages_tv)
        val user_info = getSharedPreferences("user_info", MODE_PRIVATE)

        val username: String = user_info.getString("username","User 1") ?: "User1"
        val amount: String = user_info.getString("amount","5") ?: "5"

        val username_bin = username.padEnd(10,' ').take(10).toByteArray(Charsets.UTF_8)
        val amount_bin = amount.toByteArray(Charsets.UTF_8)
        val data_bin = username_bin + amount_bin

        val advertiser = bluetoothAdapter.bluetoothLeAdvertiser

        send_msg_btn.setOnClickListener()
        {
            val message = msg_text_et.text.toString()
            messages_tv.append("\n[" + user_info.getString("username","User1") + "]  " + message)
            msg_text_et.setText("")
        }

        val serviceDataUuid = ParcelUuid(UUID.fromString("0000A7F3-0000-1000-8000-00805F9B34FB"))

        val settings = AdvertiseSettings.Builder()
            .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
            .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
            .setConnectable(true)
            .build()

        val data = AdvertiseData.Builder()
            .addServiceData(serviceDataUuid, data_bin)
            .addServiceUuid(serviceDataUuid)
            .setIncludeDeviceName(false)
            .setIncludeTxPowerLevel(false)
            .build()

        advertiser.startAdvertising(settings,data,object : AdvertiseCallback()
        {
            override fun onStartSuccess(settingsInEffect: AdvertiseSettings?) {
                messages_tv.append("\n[Host]  Adverising Successful.")
            }

            override fun onStartFailure(errorCode: Int) {
                messages_tv.append("\n[Host]  Advertising Failed. Try restarting.   " + errorCode)
            }
        })
    }
}