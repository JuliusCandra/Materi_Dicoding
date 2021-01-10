package com.dev_candra.alarmmanager.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.IntegerRes
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.dev_candra.alarmmanager.R
import java.sql.Time
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timer

class AlarmService : BroadcastReceiver() {

    companion object {
        // baris kode ini adalah konstanta untuk menentukan tipe alarm
        const val TYPE_ONE_TIME = "OneTimeAlarm"
        const val TYPE_REPEATING = "RepeatingAlarm"

        // baris kode yang digunakan untuk intent key
        const val EXTRA_MESSAGE = "message"
        const val EXTRA_TYPE = "type"

        /*
        Di sini kita menggunakan dua konstanta bertipe data integer
        untuk menentukan notif ID
        sebagai ID untuk menampilkan notifikasi kepada pengguna.
         */
        private const val ID_ONETIME = 100
        private const val ID_REPEATING = 101

        private const val DATE_FORMAT = "yyyy-MM-dd"
        private const val TIME_FORMAT = "HH:mm"
    }


    /*
        Ketika kondisi sesuai, maka akan BroadcastReceiver akan running dengan semua proses yang terdapat di dalam metode onReceive().
     */
    override fun onReceive(context: Context, intent: Intent) {
         val type = intent.getStringExtra(EXTRA_TYPE)
        val message = intent.getStringExtra(EXTRA_MESSAGE)

        val tittle = if (type.equals(TYPE_ONE_TIME,ignoreCase = true)) TYPE_ONE_TIME else TYPE_REPEATING
        val notifId = if (type.equals(TYPE_REPEATING,ignoreCase = true)) ID_ONETIME else ID_REPEATING

        showToast(context,tittle,message)
        showAlarmNotification(context,tittle,message,notifId)
    }

    private fun showToast(context: Context,tittle: String,message:String){
        Toast.makeText(context,"$tittle : $message",Toast.LENGTH_SHORT).show()
    }

    /*
    Pada kode ini kita membuat sebuah objek dari kelas AlarmManager. Kita menyiapkan
    sebuah intent yang akan menjalankan AlarmReceiver dan membawa data alarm dan pesan
     */
     fun setOneTimeAlarm(context : Context, type: String, date: String, time : String, message: String ){
        if (isDateInvalid(date,DATE_FORMAT) || isDateInvalid(time, TIME_FORMAT)) return
          val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
          val intent = Intent(context,AlarmService::class.java)
            intent.putExtra(EXTRA_MESSAGE,message)
            intent.putExtra(EXTRA_TYPE,type)

            Log.e("ONE TIME", "setOneTimeAlarm: $date,$time")
            val dateArray = date.split("-").toTypedArray()
            val timeArray = time.split(":").toTypedArray()

            val calendar = Calendar.getInstance()
            calendar.set(Calendar.YEAR, Integer.parseInt(dateArray[0]))

        /*
        Mengapa kode di atas, data kita kurangi 1? Misal tanggal yang kita masukkan adalah 2016-09-27. Jika kita pecah, maka kita akan memperoleh nilai 2016 (tahun), 9 (bulan), dan 27 (hari).

Masalahnya adalah, nilai bulan ke 9 pada kelas Calendar bukanlah bulan September. Ini karena indeksnya dimulai dari 0. Jadi, untuk memperoleh bulan September, maka nilai 9 tadi harus kita kurangi 1.
         */
            calendar.set(Calendar.MONTH,Integer.parseInt(dateArray[1]) - 1)
            calendar.set(Calendar.DAY_OF_MONTH,Integer.parseInt(dateArray[2]))
            calendar.set(Calendar.HOUR_OF_DAY,Integer.parseInt(timeArray[0]))
            calendar.set(Calendar.MINUTE,Integer.parseInt(timeArray[1]))
            calendar.set(Calendar.SECOND,0)

        // Intent yang dibuat akan dieksekusi ketika waktu alarm sama dengan waktu pada Sistem Android.
        // Disini komponen PendingIntent akan diberikan kepada BroadcastReceover
            val pendingIntent = PendingIntent.getBroadcast(context, ID_ONETIME,intent,0)

            /*
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent); adalah kita memasang alarm yang dibuat dengan tipe RTC_WAKEUP. Tipe alarm ini dapat membangunkan peranti (jika dalam posisi sleep) untuk menjalankan obyek PendingIntent.
             */
            alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.timeInMillis,pendingIntent)
            Toast.makeText(context,"One time alarm set up",Toast.LENGTH_SHORT).show()
    }

    // Metode ini digunakan untuk validasi date and time
    private fun isDateInvalid(date: String, format : String) : Boolean {
        return try {
            val df = SimpleDateFormat(format, Locale.getDefault())
            df.isLenient = false
            df.parse(date)
            false
        }catch (e: ParseException){
            true
        }
    }


    private fun showAlarmNotification(context: Context,tittle: String,message: String, notifId: Int){
        val channelId = "Channel_1"
        val channelName = "AlarmManager channel"

        val notificationCompat = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context,channelId)
            .setSmallIcon(R.drawable.ic_baseline_access_time_24)
            .setContentTitle(tittle)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context,android.R.color.transparent))
            .setVibrate(longArrayOf(1000,1000,1000,1000,1000))
            .setSound(alarmSound)


        /*
        Untuk android Oreo ke atas perlu menambahkan notification channel

         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId,channelName,NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(1000,1000,1000,1000,1000)

            builder.setChannelId(channelId)

            notificationCompat.createNotificationChannel(channel)
        }

        val notification = builder.build()
        notificationCompat.notify(notifId,notification)
    }


    fun setRepeatingAlarm(context : Context,type: String,time : String,message : String) {
        // Validasi inputan waktu terlebih dahulu
        if (isDateInvalid(time, TIME_FORMAT)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmService::class.java)
        intent.putExtra(EXTRA_MESSAGE, message)
        val putExtra = intent.putExtra(EXTRA_TYPE, type)

        val timeArray = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }

        // Kode ini digunakan untuk membuat waktu
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeArray[0]))
        calendar.set(Calendar.MINUTE, Integer.parseInt(timeArray[1]))
        calendar.set(Calendar.SECOND, 0)

        val pendingIntent = PendingIntent.getBroadcast(context, ID_REPEATING, intent, 0)

        // Baris dibawah ini akan menjalankan objek pendingintent pada setiap waktu yang ditentukan
        // dalam millisecond dengan interval perhari.
        /*
        Pengguna metode setIneexactRepeating() adalah pilihan lebih tepat karena Android akan menjalankan
        alarm ini secara bersamaan dengan alarm lain.
         */
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Toast.makeText(context, "Repeating alarm set up", Toast.LENGTH_SHORT).show()
    }

    fun cancelAlarm(context: Context,type: String){
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context,AlarmService::class.java)
        val requestCode = if (type.equals(TYPE_ONE_TIME,ignoreCase = true)) ID_ONETIME else ID_REPEATING
        val pendingIntent = PendingIntent.getBroadcast(context,requestCode,intent,0)
        pendingIntent.cancel()

        alarmManager.cancel(pendingIntent)

        Toast.makeText(context,"Repeating alarm dibatalkan",Toast.LENGTH_SHORT).show()
    }

    // Gunakan metode ini untuk mengecek apakah alarm tersebut sudah terdaftar di alarm manager
    fun isAlarmSet(context: Context,type: String): Boolean{
        val intent = Intent(context,AlarmService::class.java)
        val requestCode = if (type.equals(TYPE_ONE_TIME,ignoreCase = true)) ID_ONETIME else ID_REPEATING

        return PendingIntent.getBroadcast(context,requestCode,intent,PendingIntent.FLAG_NO_CREATE) != null
    }
    
}
