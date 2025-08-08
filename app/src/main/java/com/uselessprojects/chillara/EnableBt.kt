package com.uselessprojects.chillara

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.OnBackPressedCallback
import android.bluetooth.BluetoothManager
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts

class EnableBt: AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.enable_bt_page)

        val enable_bt_btn: Button = findViewById(R.id.enable_bt_btn)

        enable_bt_btn.setOnClickListener()
        {
            promptEnableBluetooth()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true)
        {
            override fun handleOnBackPressed() {
                return
            }
        })
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy {
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }

    private fun promptEnableBluetooth() {
        if (!bluetoothAdapter.isEnabled) {
            Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE).apply {
                bluetoothEnablingResult.launch(this)
            }
        }
        else
        {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    private val bluetoothEnablingResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK)
        {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (bluetoothAdapter.isEnabled)
            startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus && bluetoothAdapter.isEnabled)
            startActivity(Intent(this, MainActivity::class.java))
    }
}