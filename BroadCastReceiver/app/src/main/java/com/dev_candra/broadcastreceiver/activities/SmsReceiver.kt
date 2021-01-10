package com.dev_candra.broadcastreceiver.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.gsm.SmsManager
import android.telephony.gsm.SmsMessage
import android.util.Log


class SmsReceiver : BroadcastReceiver(){

    private val tag = SmsReceiver::class.java.simpleName

    // Receiver akan memproses metadata dari sms yang masuk
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras
        try {
            if (bundle != null) {
                val pdusObj = bundle?.get("pdus") as Array<Any>
                for (aPdusObj in pdusObj) {
                    val currentMessage = getIncomingMessage(aPdusObj, bundle)
                    val senderNum = currentMessage.displayOriginatingAddress
                    val message = currentMessage.displayMessageBody
                    Log.d(tag, "senderNum : $senderNum; message: $message")
                    val showSmsIntent = Intent(context, SmsActivity::class.java)
                    showSmsIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    showSmsIntent.putExtra(SmsActivity.EXTRA_SMS_NO, senderNum)
                    showSmsIntent.putExtra(SmsActivity.EXTRA_SMS_MESSAGE, message)
                    context?.startActivity(showSmsIntent)
                }
            }
        }catch (e : Exception){
            Log.d(tag,"Exception smsReceiver$e")
        }
    }


    private fun getIncomingMessage(aObject: Any, bundle: Bundle): android.telephony.SmsMessage
    {
        val currentSMS: android.telephony.SmsMessage
        if (Build.VERSION.SDK_INT >= 23){
            val format = bundle.getString("format")
            currentSMS = android.telephony.SmsMessage.createFromPdu(aObject as ByteArray,format)
        }else currentSMS = android.telephony.SmsMessage.createFromPdu(aObject as ByteArray)
    return currentSMS
    }

    /*
    Kesimpulan
    Selanjutnya ReceiverActivity akan dijalankan dengan membawa data melalui sebuah intent showsSmsIntent
    Kita memanfaatkan fasilitas yang terdapat pada kelas SmsManager dan SmsMesssage untuk melakukan pemrosesan SMS. Untuk memperoleh obyek dari kelas SmsMessage, yaitu obyek currentMessage,
    kita menggunakan metode getIncomingMessage(). Metode ini akan mengembalikan currentMessage berdasarkan OS yang dijalankan oleh perangkat Android. Hal ini perlu dilakukan karena metode SmsMessage.createFromPdu((object);
    sudah deprecated di peranti dengan OS Marshmallow atau versi setelahnya.

Flag pada showSmsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); akan menjalankan Activity pada task yang berbeda. Bila Activity tersebut sudah ada di dalam stack, maka ia akan ditampilkan ke layar.

     */
}