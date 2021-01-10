package com.dev_candra.broadcastreceiver.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.dev_candra.broadcastreceiver.R
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    // Variabel yang digunakan untuk merespon broadcast
    private lateinit var downloadReceiver: BroadcastReceiver

    companion object{
        // variabel yang digunakan untuk merespon broadcast
        const val ACTION_DOWNLOAD_STATUS = "download_status"
        private const val SMS_REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolBar()
        setAksiView()

        downloadReceiver = object : BroadcastReceiver(){
            // maka metode onReceive() akan merespon untuk melakukan proses didalamnya
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d(DownloadReceiver.TAG, "onReceive: Download Selesai")
                Toast.makeText(context,"Download Selesai",Toast.LENGTH_SHORT).show()
            }
        }
        /*
        Dua baris kode yang digunakan untuk meregister recevier
         */
        val downloadIntentFilter = IntentFilter(ACTION_DOWNLOAD_STATUS)
        registerReceiver(downloadReceiver,downloadIntentFilter)
    }

    private fun setToolBar(){
        supportActionBar?.title = resources.getString(R.string.developer)
        supportActionBar?.subtitle = resources.getString(R.string.app_name)
    }

    private fun setAksiView(){
        btn_permission.setOnClickListener {
            PermissionManager.check(this,android.Manifest.permission.RECEIVE_SMS, SMS_REQUEST_CODE)
        }
        btn_download.setOnClickListener {
            val downloadServiceIntent  = Intent(this,DownloadReceiver::class.java)
            startService(downloadServiceIntent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == SMS_REQUEST_CODE){
            when (PackageManager.PERMISSION_GRANTED) {
                grantResults[0] -> Toast.makeText(this,"Sms Receiver Permission Diterima",Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this,"Sms Receiver Permission Ditolak",Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Melakukan penghancuran activity
    override fun onDestroy() {
        super.onDestroy()
        // Mencopot pemasangan receiver pada activity
        unregisterReceiver(downloadReceiver)
    }
}