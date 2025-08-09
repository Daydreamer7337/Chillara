package com.uselessprojects.chillara

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattServer
import android.bluetooth.BluetoothGattServerCallback
import android.bluetooth.BluetoothGattService
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.AdvertiseData
import android.bluetooth.le.AdvertiseSettings
import android.content.Context
import android.os.ParcelUuid
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.util.UUID
import android.bluetooth.le.AdvertiseCallback
import android.util.Log
import androidx.annotation.RequiresPermission
import java.nio.charset.Charset

private val CHAT_SERVICE_UUID = UUID.fromString("0000A7F3-0000-1000-8000-00805F9B34FB")
private val CHAT_MESSAGE_UUID = UUID.fromString("abcd1234-ab12-cd34-ef56-1234567890ab")
private val UPI_UUID = UUID.fromString("41f884d4-1db7-49c2-866e-ff2c50f700d0")

class ActiveBroadcast: AppCompatActivity()
{
    private var gattServer: BluetoothGattServer? = null
    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter

    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH_ADVERTISE, Manifest.permission.BLUETOOTH_CONNECT])
    override fun onCreate(savedInstanceState: Bundle?)
    {
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
        startServer()

        send_msg_btn.setOnClickListener()
        {
            val message = msg_text_et.text.toString()
            messages_tv.append("\n[" + user_info.getString("username","User1") + "]  " + message)
            val chatService = gattServer?.getService(CHAT_SERVICE_UUID)
            val chatCharacteristic = chatService?.getCharacteristic(CHAT_MESSAGE_UUID)

            if (chatCharacteristic != null) {
                val messageBytes = message.toByteArray(Charsets.UTF_8)
                chatCharacteristic.value = messageBytes

                // Notify all connected devices
                // Get the BluetoothManager
                val btManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager

// Get the list of connected devices using GATT profile
                val connectedDevices = btManager.getConnectedDevices(BluetoothProfile.GATT)

// Now send notification to each device
                connectedDevices.forEach { device ->
                    gattServer?.notifyCharacteristicChanged(device, chatCharacteristic, false)
                }

            }
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

        messages_tv.append("\n"+serviceDataUuid)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun startServer() {
        gattServer = bluetoothManager.openGattServer(this, gattServerCallback)
        val service = BluetoothGattService(CHAT_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY)

        // Add a characteristic that supports read, write, and notify
        val chatCharacteristic = BluetoothGattCharacteristic(
            CHAT_MESSAGE_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ or BluetoothGattCharacteristic.PROPERTY_WRITE or
                    BluetoothGattCharacteristic.PROPERTY_NOTIFY,
            BluetoothGattCharacteristic.PERMISSION_READ or BluetoothGattCharacteristic.PERMISSION_WRITE
        )

        val upiCharacteristic = BluetoothGattCharacteristic(
            UPI_UUID,
            BluetoothGattCharacteristic.PROPERTY_READ,
            BluetoothGattCharacteristic.PERMISSION_READ
        )

        service.addCharacteristic(chatCharacteristic)
        service.addCharacteristic(upiCharacteristic)
        gattServer?.addService(service)
    }

    private val gattServerCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            // Log connections and disconnections (newState: STATE_CONNECTED, STATE_DISCONNECTED)
            Log.d("GATT", "Connection state changed: $newState")
        }

        // Respond to read requests sent by clients (send server's message)
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onCharacteristicReadRequest(
            device: BluetoothDevice?, requestId: Int, offset: Int,
            characteristic: BluetoothGattCharacteristic?
        ) {
            if (characteristic?.uuid == CHAT_MESSAGE_UUID) {
                // Example static server message
                val value = "Hello from server".toByteArray(Charset.forName("UTF-8"))
                gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value)
            }
        }

        // Handle incoming write requests (from client), echo message using notifications
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice?, requestId: Int, characteristic: BluetoothGattCharacteristic?,
            preparedWrite: Boolean, responseNeeded: Boolean, offset: Int, value: ByteArray?
        ) {
            if (characteristic?.uuid == CHAT_MESSAGE_UUID && value != null) {
                val message = String(value, Charset.forName("UTF-8"))
                Log.d("GATT", "Received from client: $message")
                runOnUiThread {
                    val messages_tv: TextView = findViewById(R.id.messages_tv)
                    messages_tv.append("\n[Client]  " + message)
                }
                // Broadcast the message back to the client as a notification (echo)
                gattServer?.notifyCharacteristicChanged(device, characteristic.apply {
                    setValue(value)
                }, false)
                // If a response is requested, send success
                if (responseNeeded) {
                    gattServer?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, value)
                }
            }
        }
    }

    public fun stopBroadcast()
    {
        finish()
    }
}