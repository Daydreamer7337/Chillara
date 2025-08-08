package com.uselessprojects.chillara

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BroadcastBLE: AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.broadcast_page)

        val user_info = getSharedPreferences("user_info", MODE_PRIVATE)
        val edit_info = user_info.edit()

        val advertise_btn: Button = findViewById(R.id.advertise_btn)
        val sharing_amount_et: EditText = findViewById(R.id.sharing_amount_et)

        sharing_amount_et.setText(user_info.getString("amount", "5"))

        advertise_btn.setOnClickListener()
        {
            if (sharing_amount_et.text.toString().contains(".")) {
                Toast.makeText(this, "Integer Values Only.", Toast.LENGTH_SHORT).show()
            } else if (sharing_amount_et.text.toString().toInt() <= 0) {
                Toast.makeText(this, "Amount should be greater than 0", Toast.LENGTH_SHORT).show()
            } else {
                edit_info.putString("amount", sharing_amount_et.text.toString())
                edit_info.apply()
                startActivity(Intent(this, ActiveBroadcast::class.java))
            }
        }
    }
}