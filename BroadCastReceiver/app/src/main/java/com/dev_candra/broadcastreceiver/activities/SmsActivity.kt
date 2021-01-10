package com.dev_candra.broadcastreceiver.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dev_candra.broadcastreceiver.R
import kotlinx.android.synthetic.main.sms_layout.*

class SmsActivity : AppCompatActivity(), View.OnClickListener{

    companion object {
        const val EXTRA_SMS_NO = "extra_sms_no"
        const val EXTRA_SMS_MESSAGE = "extra_sms_message"
    }

    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sms_layout)
        setToolbar()
        setAksiView()

        title = getString(R.string.incoming_message)

        val senderNo = intent.getStringExtra(EXTRA_SMS_NO)
        val senderMessage = intent.getStringExtra(EXTRA_SMS_MESSAGE)

        tv_from.text = senderNo
        tv_message.text = senderMessage
    }

    private fun setToolbar(){
        supportActionBar?.title = resources.getString(R.string.developer)
        supportActionBar?.subtitle = resources.getString(R.string.app_name)
    }

    private fun setAksiView(){
        btn_close.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_close -> {
                finish()
            }
        }
    }



}