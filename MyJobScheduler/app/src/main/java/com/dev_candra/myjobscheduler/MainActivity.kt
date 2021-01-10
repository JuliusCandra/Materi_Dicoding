package com.dev_candra.myjobscheduler

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object{
        private const val JOB_ID = 10
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setOnClickView()

    }

    private fun setOnClickView(){
        btn_start.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_start -> {
                startJob()
            }
            R.id.btn_cancel -> {
                cancelJob()
            }
        }
    }

    private fun startJob(){
        if (isJobRunning(this)){
            Toast.makeText(this,"Job Service is already scheduled",Toast.LENGTH_SHORT).show()
            return
        }
        val mServiceComponent = ComponentName(this,GetCurrentWeatherJobService::class.java)

        val builder = JobInfo.Builder(JOB_ID,mServiceComponent)

        /*
        Kondisi network,
        NETWORK_TYPE_ANY, berarti tidak ada ketentuan tertentu
        NETWORK_TYPE_UNMETERED, adalah network yang tidak dibatasi misalnya wifi
         */
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)



        /*
        menentukan apakah job akan dijalankan ketika perangkat dalam keadaan sedang digunakan atau tidak
         */
        builder.setRequiresDeviceIdle(false)

        /*
        menentukan apakah job akan dijalankan ketika perangkat dalam keadaan sedang digunakan atau tidak
         */
        builder.setRequiresCharging(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            /*
            set berapa interval waktu kapan job akan dijalankan. Ini bisa kita gunakan untuk menjalankan job yang sifatnya repeat atau berulang.
             */
            builder.setPeriodic(900000) // 15 menit
        }else {
            builder.setPeriodic(180000) // 3 menit
        }

        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.schedule(builder.build())
        Toast.makeText(this,"Job Service started",Toast.LENGTH_SHORT).show()

    }

    private fun cancelJob(){
        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.cancel(JOB_ID)
        Toast.makeText(this,"Job Service canceled",Toast.LENGTH_SHORT).show()

    }

    /*
    Method ini digunakan untuk mengecek apakah job sudah berjalan atau belum
    sehingga tidal dibuat secara berulang - ulang
     */
    private fun isJobRunning(context: Context) : Boolean {
        var isScheduled = false

        val scheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        for (jobInfo in scheduler.allPendingJobs){
            if (jobInfo.id == JOB_ID){
                isScheduled = true
                break
            }
        }
        return isScheduled
    }
}

/*
Kesimpulan
setOverrideDeadline, set waktu deadline job itu akan di jalankan. Jika kita menggunakan ketentuan ini, maka jika dalam waktu yang telah kita tentukan job masih belum berjalan maka job tersebut akan dipaksa untuk dijalankan.
 "weather": [
  {
    "id": 803,
    "main": "Clouds",
    "description": "broken clouds",
    "icon": "04d"
  }
]
// Cara untuk mengambil data jsonnya
val jsonObeject = String(response)
val responObject = jsonObject.getJSONArray("weather")
var main - responObject.getJSONObject(0).getString("main")

"sys": {
  "type": 3,
  "id": 257403,
  "message": 0.1707,
  "country": "ID",
  "sunrise": 1475102307,
  "sunset": 1475146037
}
   Cara untuk mengambil data jsonnya
   val response = String(response)
   val data1 = response.getJSONObject("sys").getString("main")

 */