package com.uselessprojects.chillara

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.os.ParcelUuid
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.UUID
import android.Manifest
import android.bluetooth.BluetoothGatt

class BleScanner: AppCompatActivity()
{
    private val CHAT_SERVICE_UUID = UUID.fromString("0000A7F3-0000-1000-8000-00805F9B34FB")
    private val CHAT_MESSAGE_UUID = UUID.fromString("abcd1234-ab12-cd34-ef56-1234567890ab")
    private var bluetoothGatt: BluetoothGatt? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.scan_results_page)

        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        val targetedParcelUUID = ParcelUuid.fromString("0000A7F3-0000-1000-8000-00805F9B34FB")
        val recyclerview: RecyclerView = findViewById(R.id.recyclerview)
        recyclerview.layoutManager = LinearLayoutManager(this)

        startBleScan()
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    public val data = ArrayList<Results>()
    private var previous_find: String = "johnson1"

    private val targetParcelUuid = ParcelUuid(UUID.fromString("0000A7F3-0000-1000-8000-00805F9B34FB"))

    private val bleScanner by lazy {
        bluetoothAdapter.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()

    private val filter = ScanFilter.Builder().setServiceUuid(targetParcelUuid).build()

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val scanRecord = result.scanRecord ?: return
            val serviceData: ByteArray? = scanRecord.getServiceData(targetParcelUuid)
            val recyclerview: RecyclerView = findViewById(R.id.recyclerview)

            with(result.device) { Log.i("ScanCallback", "Found BLE device! Name: ${name ?: "Unnamed"}, address: $address") }

            if (serviceData != null && serviceData.size >= 10)
            {
                // First 10 bytes -> name; remaining bytes -> amount
                val nameBytes = serviceData.copyOfRange(0, 10)
                val amountBytes = serviceData.copyOfRange(10, serviceData.size)
                val name = String(nameBytes, Charsets.UTF_8)
                val amount = String(amountBytes, Charsets.UTF_8)
                val resultItem = Results(name, amount, result.device)
                if(name != previous_find)
                {
                    data.add(resultItem)
                    val adapter = resultAdapter(data)
                    {
                        val intent = Intent(this@BleScanner, ConnectedServer::class.java)
                        intent.putExtra("bt_device",result.device)
                        intent.putExtra("sender_name",name)
                        startActivity(intent)
                    }
                    recyclerview.adapter = adapter
                    previous_find = name
                }
                Log.i("ScanCallback","name: $name , amount: $amount")
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    private fun startBleScan() {
        bleScanner.startScan(listOf(filter), scanSettings, scanCallback)
    }
}