package com.dev_candra.workmanager


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationBuilderWithBuilderAccessor
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.SyncHttpClient
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.lang.Exception
import java.text.DecimalFormat

class MyWorker(context: Context,workerParameters: WorkerParameters): Worker(context,workerParameters){

    companion object {
        private val TAG = MyWorker::class.java.simpleName
        const val API_KEY = "cfb986a544f79d4c3ffdf40668c3e2af"
        const val EXTRA_CITY = "Medan"
        const val Notification_ID = 1
        const val Channel_ID = "channel_01"
        const val Channel_Name = "candra channel"
    }
    
    private var resultStatus : Result? = null

    // Metod yang dipanggil ketika WorkManager berjalan. Kode didalamnya akan dijalankan di
    // background thread secara otomatis.Metode ini juga mengembalikan nilai berupa Result yang berfungsi untuk mengetahui status WorkManager yang berjalan

    /*
    Result.success(), result yang menandakan berhasil.
    Result.failure(), result yang menandakan gagal.
    Result.retry(), result yang menandakan untuk mengulang task lagi.
     */
    override fun doWork(): Result {
        // Your Code
        val dataCity = inputData.getString(EXTRA_CITY)
        return getCurrentWeather(dataCity)
    }

    private fun getCurrentWeather(dataCity: String?): Result {
        Log.d(TAG, "getCurrentWeather: Mulai...")
        val client = SyncHttpClient()
        val url = "https://api.openweathermap.org/data/2.5/weather?q=$dataCity&appid=$API_KEY"
        Log.d(TAG, "getCurrentWeather: $url")
        client.post(url,object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                // Your Code
                val result = responseBody?.let { String(it) }
                Log.d(TAG, "onSuccess: $result")
                try {
                    val responObject = JSONObject(result)
                    val currentWeather = responObject.getJSONArray("weather").getJSONObject(0).getString("main")
                    val description = responObject.getJSONArray("weather").getJSONObject(0).getString("description")
                    val tempKelvin = responObject.getJSONObject("main").getDouble("temp")
                    val celcius = tempKelvin - 273
                    val temperature = DecimalFormat("##.##").format(celcius)
                    val tittle = "Current Weather in $dataCity"
                    val message = "$currentWeather, $description dengan $temperature celcius"
                    showNotifcation(tittle,message)
                    Log.d(TAG, "onSuccess: Selesai...")
                    resultStatus = Result.success()
                }catch (e: Exception){
                    e.message?.let { showNotifcation("Get Current Weather Not Success", it) }
                    Log.d(TAG, "onSuccess: Gagal....")
                    resultStatus = Result.failure()
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
              // Your Code
                Log.d(TAG, "onFailure: Gagal...")
                // ketika proses gagal, maka jobFinished diset dengan parameter true. Yang artinya job perlu di reschedule
                error?.message?.let { showNotifcation("Get Current Weather Failed", it) }
                resultStatus = Result.failure()
            }

        })
        return resultStatus as Result
    }

    private fun showNotifcation(tittle: String, message: String) {
        val notifactionManager = applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext,
            Channel_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(tittle)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            val channel = NotificationChannel(Channel_ID, Channel_Name,NotificationManager.IMPORTANCE_HIGH)
            notification.setChannelId(Channel_ID)
            notifactionManager.createNotificationChannel(channel)
        }
        notifactionManager.notify(Notification_ID,notification.build())
    }

}