package com.dev_candra.androidnotification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.NotificationCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "dicoding channel"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    //aksi untuk onClick pada button
    @SuppressLint("ServiceCast")
    fun sendNotification(view: View) {
        /*
        Kode di atas digunakan untuk mengirim notifikasi sesuai dengan id yang kita berikan. Fungsi id di sini nanti juga bisa untuk membatalkan notifikasi yang sudah muncul.
         */
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_baseline_notifications_24))
                .setContentTitle(resources.getString(R.string.content_title))
                .setContentText(resources.getString(R.string.content_text))
                .setSubText(resources.getString(R.string.subtext))
                .setAutoCancel(true)

        /*

        Kode di atas digunakan untuk menambahkan elemen apa saja yang akan ditampikan pada notifikasi seperti icon, action, title, content, dan lain-lain.

Dalam notification builder terdapat beberapa komponen sebagai berikut:

Small Icon : Ikon ini yang akan muncul pada status bar (wajib ada).

Large Icon : Ikon ini yang akan muncul di sebelah kiri dari text notifikasi.

Content Intent : Intent ini sebagai action jika notifikasi ditekan.

Content Title : Judul dari notifikasi (wajib ada).

Content Text : Text yang akan muncul di bawah judul notifikasi (wajib ada).

Subtext : Text ini yang akan muncul di bawah content text.

Auto Cancel : Digunakan untuk menghapus notifikasi setelah ditekan.
       */

        /*
           Untuk android Oreo ke atas perlu menambahkan notification channel
        Untuk memunculkan notifkasi pada OS Oreo ke atas, Anda harus membuat channel agar notifikasi bisa berjalan.

        */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create or update. */
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = CHANNEL_NAME
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }

        val notification = mBuilder.build()

        mNotificationManager.notify(NOTIFICATION_ID, notification)
    }


     fun setndNotificationPendingIntent(view: View){
         /*
         Digunakan untuk memberikan action jika notifikasi disentuh. Selain ke halaman Web, anda juga bisa mengganti intent tersebut ke Activity dengan menggunakan Intent untuk memanggil activity seperti biasanya.
          */
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://dicoding.com"))
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_baseline_notifications_24))
                .setContentTitle(resources.getString(R.string.content_title))
                .setContentText(resources.getString(R.string.content_text))
                .setSubText(resources.getString(R.string.subtext))
                .setAutoCancel(true)
        /*
         Untuk android Oreo ke atas perlu menambahkan notification channel
         */
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

             /* Create or update. */
             val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)

             mBuilder.setChannelId(CHANNEL_ID)

             mNotificationManager.createNotificationChannel(channel)
         }

         val notification = mBuilder.build()

         mNotificationManager.notify(NOTIFICATION_ID, notification)

    }

}
