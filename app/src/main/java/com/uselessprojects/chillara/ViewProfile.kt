package com.uselessprojects.chillara

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ViewProfile: AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_page)

        val home_btn: Button = findViewById(R.id.home_btn)
        val edit_profile_btn: Button = findViewById(R.id.edit_profile_btn)
        val user_info = getSharedPreferences("user_info", MODE_PRIVATE)
        val username_tv: TextView = findViewById(R.id.username_tv)
        val upi_id_tv: TextView = findViewById(R.id.upi_id_tv)
        val user_department_tv: TextView = findViewById(R.id.user_department_tv)

        username_tv.text = user_info.getString("username","User 1")
        upi_id_tv.text = user_info.getString("upi_id","user@anybank")
        user_department_tv.text = user_info.getString("department","Your Department")

        home_btn.setOnClickListener()
        {
            startActivity(Intent(this, MainActivity::class.java))
        }
        edit_profile_btn.setOnClickListener()
        {
            startActivity(Intent(this, EditProfile::class.java))
        }
    }
}