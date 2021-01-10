package com.dev_candra.service.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log

class MyBoundService : Service() {

    companion object {
        private val TAG = MyBoundService::class.java.simpleName
    }

    private var mBinder = MyBinder()
    private val startTimer = System.currentTimeMillis()


    /*
    Setelah onCreate() terjadi maka akan lanjut ke onBind()
     */
    override fun onBind(intent: Intent): IBinder {
       Log.d(TAG,"omBind")
        // Timer akan berjalan pada waktu service dijalankan di onBind
        mTimer.start()
        return mBinder
    }

    /*
    // Kode ini digunakan untuk memanggil metode onServiceConnected untuk memanggil kelas service
    // Kelas MyBinder yang diberi turunan kelas Binder, mempunyai fungsi untuk melakukan mekanisme pemanggilan prosedur jarak jauh.
     */
   internal inner class MyBinder: Binder() {
        val getService : MyBoundService = this@MyBoundService
    }

    /*
    Ketika CountDownTimer dijalankan, countdown timer akan berjalan
    sampai 100.000 milisecond atau 100 detik. Intervalnya setiap 1.000 milisecond atau 1 detik akan menampilkan log.
     Hitungan mundur tersebut berfungsi untuk melihat proses terikatnya kelas MyBoundService ke MainActivity.


     */
    private var mTimer : CountDownTimer = object  : CountDownTimer(100000,1000){
        override fun onFinish() {
            TODO("Not yet implemented")
        }

        override fun onTick(millisUntilFinished: Long) {
            val elapsedTime = System.currentTimeMillis() - startTimer
            Log.d(TAG,"onTick : $elapsedTime")
        }
    }

    /*
    Metode onCreate() dipanggil ketika memulai pembentukan kelas MyBoundService.
     */
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"onCreate")
    }

    /*
    Pada metode onBind(), service akan berjalan dan diikatkan atau ditempelkan dengan activity pemanggil. Pada metode ini juga, mTimer akan mulai berjalan.
     */

    /*
    Kode dibawah ini berfungsi untuk melepaskan service dari activity penaggil. Kemudia setelah
    metode onUnbind dipanggil, maka ia akan memanggil metode onDestroy() dikelas MyBoundService
     */
    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG,"onUnbind: ")
        mTimer.cancel()
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        Log.d(TAG,"onRebind: ")
    }
}
