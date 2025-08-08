package com.uselessprojects.chillara

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val profile_btn: Button = findViewById(R.id.view_profile_btn)
        val shareST_page_btn: Button = findViewById(R.id.shareST_page_btn)
        val scanST_page_btn: Button = findViewById(R.id.scanST_page_btn)

        profile_btn.setOnClickListener()
        {
            startActivity(Intent(this, ViewProfile::class.java))
        }

        if((!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) && (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) && (!hasPermission(Manifest.permission.BLUETOOTH_ADVERTISE)))
        {
            startActivity(Intent(this, EnablePermissions::class.java))
        }
        else if(!bluetoothAdapter.isEnabled)
        {
            startActivity(Intent(this, EnableBt::class.java))
        }
        shareST_page_btn.setOnClickListener()
        {
            startActivity(Intent(this, BroadcastBLE::class.java))
        }
    }

    fun Context.hasPermission(permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    private val bluetoothAdapter: BluetoothAdapter by lazy{
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
}