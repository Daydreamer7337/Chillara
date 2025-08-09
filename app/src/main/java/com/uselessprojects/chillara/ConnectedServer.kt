package com.uselessprojects.chillara

import android.bluetooth.BluetoothGatt
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import java.util.UUID
import android.Manifest
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.nio.charset.Charset
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

private val CHAT_SERVICE_UUID = UUID.fromString("0000A7F3-0000-1000-8000-00805F9B34FB")
private val CHAT_MESSAGE_UUID = UUID.fromString("abcd1234-ab12-cd34-ef56-1234567890ab")
private val UPI_UUID = UUID.fromString("41f884d4-1db7-49c2-866e-ff2c50f700d0")
private var upiId: String? = null

class ConnectedServer: AppCompatActivity()
{
    private val CHAT_SERVICE_UUID = UUID.fromString("0000A7F3-0000-1000-8000-00805F9B34FB")
    private val CHAT_MESSAGE_UUID = UUID.fromString("abcd1234-ab12-cd34-ef56-1234567890ab")

    private var bluetoothGatt: BluetoothGatt? = null
    private lateinit var sender_name: String

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.connected_to_host_page)

        val ble_device: BluetoothDevice = intent.getParcelableExtra<BluetoothDevice>("bt_device")!!
        sender_name = intent.getStringExtra("sender_name")!!
        val client_message_tv: TextView = findViewById(R.id.client_messages_tv)
        val client_message_et: EditText = findViewById(R.id.client_msg_text_et)
        val client_send_btn: Button = findViewById(R.id.client_send_msg_btn)
        val client_upi_btn: Button = findViewById(R.id.copy_upi_btn)
        val user_info = getSharedPreferences("user_info", MODE_PRIVATE)
        client_message_tv.append(ble_device.toString())

        connectToGattServer(ble_device)

        client_send_btn.setOnClickListener()
        {
            val message = client_message_et.text.toString()
            sendMessageToGattServer(message)
            client_message_tv.append("\n[" + user_info.getString("username","User1") + "]  " + message)
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {

        // Called when connection state changes (connect/disconnect)
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("GATT_CLIENT", "Connected to GATT server, discovering services")
                // Start service discovery when connected
                gatt?.discoverServices()
                runOnUiThread {
                    Toast.makeText(this@ConnectedServer, "Connected to device", Toast.LENGTH_SHORT).show()
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("GATT_CLIENT", "Disconnected from GATT server")
                runOnUiThread {
                    Toast.makeText(this@ConnectedServer, "Disconnected", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Called when services are discovered on the server
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("GATT_CLIENT", "Services discovered")
                // Enable notifications on the chat characteristic to receive data
                val characteristic = gatt?.getService(CHAT_SERVICE_UUID)?.getCharacteristic(CHAT_MESSAGE_UUID)
                if (characteristic != null) {
                    gatt.setCharacteristicNotification(characteristic, true)
                    // Sometimes you also need to write the CCCD descriptor to enable notifications on certain devices
                    val descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
                    descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    if (descriptor != null) {
                        gatt.writeDescriptor(descriptor)
                    }
                }
                runOnUiThread {
                    Toast.makeText(this@ConnectedServer, "Ready to chat!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.w("GATT_CLIENT", "Service discovery failed with status $status")
            }
        }

        // Called when a characteristic’s value changes (notification from server)
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        override fun onCharacteristicChanged(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?) {
            val client_message_tv: TextView = findViewById(R.id.client_messages_tv)
            if (characteristic?.uuid == CHAT_MESSAGE_UUID) {
                gatt?.requestMtu(517)
                // Read incoming message from server
                val receivedMessage = characteristic.value?.toString(Charset.forName("UTF-8"))
                Log.d("GATT_CLIENT", "Received message: $receivedMessage")
                runOnUiThread {
                    // Update your UI here with received message as needed
                    Toast.makeText(this@ConnectedServer, "Received: $receivedMessage", Toast.LENGTH_SHORT).show()
                    client_message_tv.append("\n[ " + sender_name + "]  " + receivedMessage)
                }
            }
        }
    }

    // -- Call this to connect to a BLE device acting as GATT server --
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connectToGattServer(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
    }

    // -- Call this to send a chat message to the connected GATT server --
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun sendMessageToGattServer(message: String) {
        val service = bluetoothGatt?.getService(CHAT_SERVICE_UUID)
        val characteristic = service?.getCharacteristic(CHAT_MESSAGE_UUID)

        if (characteristic == null) {
            Log.w("GATT_CLIENT", "Chat characteristic not found!")
            Toast.makeText(this, "Chat characteristic not found", Toast.LENGTH_SHORT).show()
            return
        }

        characteristic.value = message.toByteArray(Charset.forName("UTF-8"))
        bluetoothGatt?.writeCharacteristic(characteristic)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onDestroy() {
        super.onDestroy()
        // Clean up GATT connection when your activity closes
        bluetoothGatt?.close()
        bluetoothGatt = null
    }
}