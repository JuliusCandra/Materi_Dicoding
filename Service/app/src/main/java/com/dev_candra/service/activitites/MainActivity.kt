package com.dev_candra.service.activitites

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.View
import com.dev_candra.service.R
import com.dev_candra.service.service.MyBoundService
import com.dev_candra.service.service.MyIntentService
import com.dev_candra.service.service.MyService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var mServiceBound = false
    private lateinit var mBoundService: MyBoundService


    // Sebuah listener untuk menerima callback dari serviceConnection
    private val mServiceConnection = object : ServiceConnection {

        // Mulai terhubung dengan boundservice
        override fun onServiceDisconnected(name: ComponentName?) {
            mServiceBound = false
        }

        // Kode mulai terputus dengan boundService
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
           val myBinder = service as MyBoundService.MyBinder
            mBoundService = myBinder.getService
            mServiceBound = true
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setClickView()
        initToolbar()
    }

    private fun initToolbar(){
        supportActionBar?.title = "Candra Julius Sinaga"
        supportActionBar?.subtitle = resources.getString(R.string.app_name)
    }


    private fun setClickView(){
        btn_start_service.setOnClickListener(this)
        btn_start_bound_service.setOnClickListener(this)
        btn_stop_bound_service.setOnClickListener(this)
        btn_start_intent_service.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_start_service -> {
                val myStartService = Intent(this@MainActivity,
                    MyService::class.java)
                startService(myStartService)
            }
            R.id.btn_start_bound_service -> {
                
                //bindService adalah Kode yang digunakan untuk mengikat service dengan activity utama
                /*
                Sedangkan mBoundServiceIntent adalah sebuah intent eksplisit yang digunakan untuk menjalankan komponen dari dalam sebuah aplikasi.
                Sedangkan mServiceConnection adalah sebuah ServiceConnection berfungsi sebagai callback dari kelas MyBoundService.
                BIND_AUTO_CREATE yang membuat sebuah service jika service tersebut belum aktif.
                 */
                val mBoundServiceIntent = Intent(this@MainActivity,MyBoundService::class.java)
                bindService(mBoundServiceIntent,mServiceConnection,Context.BIND_AUTO_CREATE)
            }
            R.id.btn_stop_bound_service -> {
                // Untuk melepaskan service dari activity pemanggil. Secara tidak langsung maka ia akan
                // memanggil metode unBind yang ada di kelas MyBoundService
                unbindService(mServiceConnection)
            }
            R.id.btn_start_intent_service -> {
                val mStartIntentService = Intent(this@MainActivity,
                    MyIntentService::class.java)
                mStartIntentService.putExtra(MyIntentService.EXTRA_DURATION,5000L)
                startService(mStartIntentService)
            }
        }
    }

    /*
    Kode onDestroy() seperti yang dijelaskan di metode sebelumnya, akan memanggil unBindService atau melakukan pelepasan service dari Activity. Pemanggilan unbindService di dalam onDestroy ditujukan untuk mencegah memory leaks dari bound services.
     */
    /*
    Metode onDestroy() yang ada di MyBoundService ini berfungsi untuk melakukan penghapusan
    kelas MyBoundService dari memori. Jadi setelah service sudah terlepas dari kelas
    MainActivity, kelas MyBoundService juga terlepas dari memori android
     */
    override fun onDestroy() {
        super.onDestroy()
        if (mServiceBound){
            unbindService(mServiceConnection)
        }
    }
}

/*
Kesimpulan
BIND_ABOVE_CLIENT : yang digunakan ketika sebuah service lebih penting daripada aplikasi itu sendiri.
BIND_ADJUST_WITH_ACTIVITY : saat mengikat sebuah service dari activity, maka ia akan mengizinkan untuk menargetkan service mana yang lebih penting berdasarkan activity yang terlihat oleh pengguna.
BIND_ALLOW_OOM_MANAGEMENT : memungkinkan untuk mengikat service hosting untuk mengatur memori secara normal.
BIND_AUTO_CREATE : secara otomatis membuat service selama binding-nya aktif.
BIND_DEBUG_UNBIND : berfungsi sebagai bantuan ketika debug mengalami masalah pada pemanggilan unBind.
BIND_EXTERNAL_SERVICE : merupakan service yang terikat dengan service eksternal yang terisolasi.
BIND_IMPORTANT : service ini sangat penting bagi klien, jadi harus dibawa ke tingkat proses foreground.
BIND_NOT_FOREGROUND : pada service ini tak disarankan untuk mengubah ke tingkat proses foreground.
BIND_WAIVE_PRIORITY : service ini tidak akan mempengaruhi penjadwalan atau prioritas manajemen memori dari target proses layanan hosting.
 */