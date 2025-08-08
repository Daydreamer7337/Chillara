package com.uselessprojects.chillara

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        }
    }
}