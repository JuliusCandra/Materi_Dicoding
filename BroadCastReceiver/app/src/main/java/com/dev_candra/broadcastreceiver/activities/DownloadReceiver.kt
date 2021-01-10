package com.dev_candra.broadcastreceiver.activities

import android.app.IntentService
import android.content.Intent
import android.util.Log

class DownloadReceiver : IntentService("DownloadServce"){


    companion object {
        val TAG = DownloadReceiver::class.java.simpleName
    }

    /*
    Di sini kita akan menjalankan Intent Service yang akan melakukan proses mengunduh file secara Asynchronous di background.
    DownloadService mengambil peran di sini. Pada kenyataanya, DownloadService ini hanya melakukan proses sleep() selama 5 detik dan kemudian mem-broadcast sebuah IntentFilter dengan Action yang telah ditentukan, ACTION_DOWNLOAD_STATUS. Kodenya sebagai berikut:
     */

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent: Download Service Dijalankan")

        if (intent != null){
            try {
                Thread.sleep(5000)
            }catch (e : InterruptedException){
                e.printStackTrace()
            }

            /*
            Ketika proses pengunduhan berkas tersebut selesai, service akan
            membroadcst sebuah event dan akan ada Activity yang merespon
             */
            // Ketika baris dibawah ini dijalankan
            val notifyFinishIntent = Intent(MainActivity.ACTION_DOWNLOAD_STATUS)
            sendBroadcast(notifyFinishIntent)
        }
    }

    /*
    Kesimpulan
3 poin penting yang menjadi kesimpulan dari proses manual di atas adalah:

Registrasikan sebuah obyek BroadcastReceiver pada komponen aplikasi seperti Activity dan Fragment dan tentukan action/event apa yang ingin didengar/direspon.
Lakukan proses terkait pada metode onReceiver() ketika event atau action yang dipantau di-broadcast oleh komponen lain.
Jangan lupa untuk mencopot pemasangan obyek receiver sebelum komponen tersebut dihancurkan atau dimatikan.


     */
}