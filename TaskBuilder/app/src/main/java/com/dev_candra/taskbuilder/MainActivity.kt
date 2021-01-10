package com.dev_candra.taskbuilder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener{

    private val notifiInt = 110

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setOnClickView()
    }


    // Membuat sebuah function ketika view diklik
    private fun setOnClickView(){
        btn_open_detail.setOnClickListener(this)
        showNotification(this,getString(R.string.notification_title),getString(R.string.notification_message),notifiInt)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_open_detail -> {
                val detailIntent = Intent(this@MainActivity,DetailActivity::class.java)
                detailIntent.putExtra(DetailActivity.EXTRA_TITTLE,getString(R.string.detail_title))
                detailIntent.putExtra(DetailActivity.EXTRA_MESSAGE,getString(R.string.detail_message))
                startActivity(detailIntent)
            }
        }
    }


    // Sebuah function untuk membuat notifikasi
    private fun showNotification(context: Context,tittle: String,message: String,notifiID: Int){

        /*
        Di sini kita memanfaatkan TaskStackBuilder API untuk membuat sebuah back stack baru yang akan dimasukkan ke dalam task yang sudah ada. Ketika kelas DetailActivity dijalankan, kemudian pengguna menekan tombol back baik itu system back button maupun up button, maka pengguna akan diarahkan ke ParentActivity dari DetailActivity. ParentActivity tersebut didefinisikan di berkas AndroidManifest.xml sebagai berikut:

         */
        val notfyDetailIntent = Intent(this,DetailActivity::class.java)
        notfyDetailIntent.putExtra(DetailActivity.EXTRA_TITTLE,tittle)
        notfyDetailIntent.putExtra(DetailActivity.EXTRA_MESSAGE,message)


        val pendingIntent = TaskStackBuilder.create(this)
            .addParentStack(DetailActivity::class.java)
            .addNextIntent(notfyDetailIntent)
            .getPendingIntent(110,PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationManagerCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound  = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(tittle)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context,android.R.color.black))
            .setSound(alarmSound)
            .setVibrate(longArrayOf(1000,1000,1000,1000,1000))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID,
            CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)

            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000,1000,1000,1000,1000)

            builder.setChannelId(CHANNEL_ID)

            notificationManagerCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationManagerCompat.notify(notifiID,notification)
    }

    companion object {
        const val CHANNEL_ID = "Channel ID"
        const val CHANNEL_NAME = "Channel Name"
    }
}