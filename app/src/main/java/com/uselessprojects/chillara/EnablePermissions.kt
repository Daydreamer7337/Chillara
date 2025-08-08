package com.uselessprojects.chillara

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.OnBackPressedCallback

class EnablePermissions: AppCompatActivity()
{
    private val PERMISSION_REQUEST_CODE = 2004

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.permission_page)

        val enable_permissions_btn: Button = findViewById(R.id.enable_permission_btn)

        val enable_permission_btn: Button = findViewById(R.id.enable_permission_btn)

        enable_permission_btn.setOnClickListener()
        {
            if(!(hasPermission(Manifest.permission.BLUETOOTH_SCAN) && hasPermission(Manifest.permission.BLUETOOTH_CONNECT) && hasPermission(Manifest.permission.BLUETOOTH_ADVERTISE)))
            {
                requestBluetoothPermissions()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true)
        {
            override fun handleOnBackPressed() {
                return
            }
        })
    }

    fun Context.hasPermission(permissionType: String): Boolean
    {
        return ContextCompat.checkSelfPermission(this, permissionType) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestBluetoothPermissions(){
        ActivityCompat.requestPermissions(this,
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
            ),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val enable_permission_btn: Button = findViewById(R.id.enable_permission_btn)
        val permission_request_tv: TextView = findViewById(R.id.permission_request_tv)
        if (requestCode != PERMISSION_REQUEST_CODE) return

        val containsPermanentDenial = permissions.zip(grantResults.toTypedArray()).any {
            it.second == PackageManager.PERMISSION_DENIED &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(this, it.first)
        }
        val containsDenial = grantResults.any { it == PackageManager.PERMISSION_DENIED }
        val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        when {
            containsPermanentDenial -> {
                permission_request_tv.text = getString(R.string.permission_denied_permanently)
                enable_permission_btn.isEnabled = false
                enable_permission_btn.visibility = View.INVISIBLE
            }
            containsDenial -> {
                requestBluetoothPermissions()
            }
            allGranted && hasPermission(Manifest.permission.BLUETOOTH_SCAN) && hasPermission(Manifest.permission.BLUETOOTH_CONNECT) && hasPermission(Manifest.permission.BLUETOOTH_ADVERTISE)  -> {
                startActivity(Intent(this, MainActivity::class.java))
            }
            else -> {
                // Unexpected scenario encountered when handling permissions
                recreate()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(hasPermission(Manifest.permission.BLUETOOTH_SCAN) && hasPermission(Manifest.permission.BLUETOOTH_CONNECT) && hasPermission(Manifest.permission.BLUETOOTH_ADVERTISE))
            startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus && hasPermission(Manifest.permission.BLUETOOTH_SCAN) && hasPermission(Manifest.permission.BLUETOOTH_CONNECT) && hasPermission(Manifest.permission.BLUETOOTH_ADVERTISE))
            startActivity(Intent(this, MainActivity::class.java))
    }
}