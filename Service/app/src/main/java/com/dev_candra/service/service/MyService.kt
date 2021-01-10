package com.dev_candra.service.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MyService : Service() {


    companion object {
        internal val TAG = MyService::class.java.simpleName
    }

    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Disconnected")
    }

    // Fungsi ketika service dijalankan
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG,"Service dijalankan...")

        // Kode yang digunakan untuk melakukan asynchronus
        GlobalScope.launch {

            // Guna untuk mengentikan / menunda corotine
            delay(3000)

            // Berfungsi untuk mematikan service di sistem android
            stopSelf()
            Log.d(TAG,"Service dihentikan")
        }
        /*
        menandakan bahwa bila service tersebut dimatikan oleh sistem android karena kekurangan
        memori, ia akan diciptakan kembali jika sudah ada memori yang bisa digunakan
        Metode onStartCommand() juga akan kembali dijalankan
         */
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"onDestroy")
    }
}
