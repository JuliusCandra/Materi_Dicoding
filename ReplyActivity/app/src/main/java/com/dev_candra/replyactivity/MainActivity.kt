package com.dev_candra.replyactivity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val buttonNotif = findViewById<Button>(R.id.button_show_notification)
        buttonNotif.setOnClickListener {
            startService(Intent(this, NotificationService::class.java))
        }
    }
}