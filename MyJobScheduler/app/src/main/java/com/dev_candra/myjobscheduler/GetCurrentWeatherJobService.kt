package com.dev_candra.myjobscheduler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.text.DecimalFormat

class GetCurrentWeatherJobService : JobService(){

    companion object {
        private const val CHANNEL_ID = "Channel_ID"
        private const val CHANNEL_NAME = "JobScheduler"
        private val TAG = GetCurrentWeatherJobService::class.java.simpleName

        // Isikan dengan nama kota
        internal const val CITY = "Medan"

        // Isikan dengan API Key dari openweathermap
        internal const val APP_ID = "cfb986a544f79d4c3ffdf40668c3e2af"
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        //Code
        Log.d(TAG, "onStopJob: StopJob")
        return true
    }

    override fun onStartJob(params: JobParameters?): Boolean {
        // Code
        Log.d(TAG, "onStartJob: StartJob")
        if (params != null) {
            getCurrentWeather(params)
        }
        return true
    }

    private fun getCurrentWeather(job: JobParameters){
        Log.d(TAG, "getCurrentWeather: Mulai....")
        val client = AsyncHttpClient()
        val url = "http://api.openweathermap.org/data/2.5/weather?q=$CITY&appid=$APP_ID"
        Log.d(TAG, "getCurrentWeather: $url")
        client.get(url,object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                // Code Success
                val result = responseBody?.let { String(it) }
                Log.d(TAG, "onSuccess: $result")
                try {
                    val responObjects = JSONObject(result)

                    val currentWeather = responObjects.getJSONArray("weather").getJSONObject(0).getString("main")
                    val description = responObjects.getJSONArray("weather").getJSONObject(0).getString("description")
                    val tempInKelvin = responObjects.getJSONObject("main").getDouble("temp")

                    val tempInCelsius = tempInKelvin - 273

                    /*
                    Kode di atas digunakan untuk memformat tampilan agar hanya ada dua nilai dibelakang koma.
                     */
                    val temperature = DecimalFormat("##.##").format(tempInCelsius)

                    val title = "Current Weather"
                    val message = "$currentWeather,$description with $temperature celsius"
                    val notifId = 100


                    showNotification(application,title,message,notifId)

                    Log.d(TAG, "onSuccess: Selesai....")
                    jobFinished(job,false)
                }catch (e : Exception){
                    Log.d(TAG, "onSuccess: Gagal.....")
                    jobFinished(job,false)
                    e.printStackTrace()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                // Code Failed
                Log.d(TAG, "onFailure: Gagal")
                jobFinished(job,true)
            }

        })
    }

    private fun showNotification(context: Context,title: String,message: String,id: Int){



        val notificationCompactManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_baseline_replay_30_24)
            .setContentText(message)
            .setColor(ContextCompat.getColor(context,android.R.color.black))
            .setVibrate(longArrayOf(1000,1000,1000,1000,1000))
            .setSound(alarmSound)

       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
           val channel = NotificationChannel(
               CHANNEL_ID, CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT
           )
           channel.enableVibration(true)
           channel.vibrationPattern = longArrayOf(1000,1000,1000,1000,1000)

           builder.setChannelId(CHANNEL_ID)

           notificationCompactManager.createNotificationChannel(channel)
       }

        val notification = builder.build()

        notificationCompactManager.notify(id, notification)
    }
}

/*
Kesimpulan
    Metode onStartJob adalah metode yang akan dipanggil ketika scheduler berjalan. Sedangkan metode onStopJob akan dipanggil ketika job sedang berjalan akan tetapi belum selesai dikarenakan kondisi nya tidak terpenuhi.
    Metode onStartJob adalah metode yang akan dipanggil ketika scheduler berjalan. Sedangkan metode onStopJob akan dipanggil ketika job sedang berjalan akan tetapi belum selesai dikarenakan kondisi nya tidak terpenuhi.
    Ketika proses berjalan di thread yang berbeda, maka proses tersebut dapat mengabarkan kapan dia telah selesai. Caranya dengan menjalankan jobFinished(). Perhatikan kode yang dicetak tebal dalam getCurrentWeather().
    Sangat penting bagi job untuk mejalankan jobFinished ketika ia sudah selesai. Bila job tidak dinyatakan selesai, perangkat Android tidak dapat masuk ke state idle kembali. Konsekuensi lainnya job ini akan memakan daya baterai yang lebih banyak.
 */