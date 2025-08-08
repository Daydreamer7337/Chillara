package com.uselessprojects.chillara

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class EditProfile: AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)

        val edit_username_et: EditText = findViewById(R.id.edit_username_et)
        val edit_upi_id_et: EditText = findViewById(R.id.edit_upi_id_et)
        val edit_department_et: EditText= findViewById(R.id.edit_department_et)

        val save_edit_profile_btn: Button = findViewById(R.id.save_edit_profile_btn)
        val cancel_edit_profile_btn: Button = findViewById(R.id.cancel_edit_profile_btn)

        val user_info = getSharedPreferences("user_info",MODE_PRIVATE)
        val edit_info = user_info.edit()

        edit_username_et.setText(user_info.getString("username","User 1"))
        edit_upi_id_et.setText(user_info.getString("upi_id","user@anybank"))
        edit_department_et.setText(user_info.getString("department","Your Department"))

        save_edit_profile_btn.setOnClickListener()
        {
            edit_info.putString("username",edit_username_et.text.toString())
            edit_info.putString("upi_id",edit_upi_id_et.text.toString())
            edit_info.putString("department",edit_department_et.text.toString())
            edit_info.apply()
            startActivity(Intent(this, ViewProfile::class.java))
        }

        cancel_edit_profile_btn.setOnClickListener()
        {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}