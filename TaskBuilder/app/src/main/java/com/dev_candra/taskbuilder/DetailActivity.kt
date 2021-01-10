package com.dev_candra.taskbuilder

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.detaiilactivity_layout.*

class DetailActivity : AppCompatActivity(){
    
     companion object{
         const val EXTRA_TITTLE = "extra_tittle"
         const val EXTRA_MESSAGE = "extra_message"
     }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detaiilactivity_layout)
        takeData()
    }

    private fun takeData(){
         val tittle = intent.getStringExtra(EXTRA_TITTLE)
         val  message = intent.getStringExtra(EXTRA_MESSAGE)
        tv_title.text = tittle
        tv_message.text = message
    }
}