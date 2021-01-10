package com.dev_candra.workmanager

import android.app.AlertDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.work.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), View.OnClickListener{


    private lateinit var periodicWorkRequest: PeriodicWorkRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbar()
        setOnClickView()
    }

    private fun setToolbar(){
        supportActionBar?.title = "Candra Julius Sinaga"
        supportActionBar?.subtitle = resources.getString(R.string.app_name)
    }

    private fun setOnClickView(){
        btnOneTimeTask.setOnClickListener(this)
        btnPeriodicTask.setOnClickListener(this)
        btnCancelTask.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        // Your Code
        when(v?.id){
            R.id.btnOneTimeTask -> startOneTimeTask()
            R.id.btnPeriodicTask -> startPeriodicTask()
            R.id.btnCancelTask -> cancelPeriodicTask()
        }
    }

    private fun startOneTimeTask(){
        textStatus.text = getString(R.string.status)

        /*
        Fungsi di atas digunakan untuk membuat one-time request. Saat membuat request, Anda bisa menambahkan data untuk dikirimkan dengan membuat object Data yang berisi data key-value, key yang dipakai di sini yaitu MyWorker.EXTRA_CITY. Setelah itu dikirimkan melalui setInputData. Kemudian untuk mendapatkan datanya di kelas Worker, Anda perlu menggunakan kode berikut:
         */
        val data = Data.Builder()
            .putString(MyWorker.EXTRA_CITY,editCity.text.toString())
            .build()

        /*
        Constraint digunakan untuk memberikan syarat kapan task ini dieksekusi, perhatikan kode di dalam MainActivity.
         */
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        /*
        OneTimeWorkRequest untuk menjalankan task sekali saja, untuk membuatnya Anda menggunakan kode berikut:
         */
        val oneTimeRequest = OneTimeWorkRequest.Builder(MyWorker::class.java)
            .setInputData(data)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueue(oneTimeRequest)
        WorkManager.getInstance().getWorkInfoByIdLiveData(oneTimeRequest.id).observe(this@MainActivity,object: Observer<WorkInfo> {
            override fun onChanged(t: WorkInfo?) {
                val status = t?.state?.name
                textStatus.append("\n" + status)
            }
        })
    }

    private fun startPeriodicTask(){
            textStatus.text = getString(R.string.status)
            val data = Data.Builder()
                .putString(MyWorker.EXTRA_CITY,editCity.text.toString())
                .build()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        /*
        PeriodicWorkRequest untuk menjalankan task secara periodic, untuk membuatnya Anda menggunakan kode berikut:
         */
            periodicWorkRequest = PeriodicWorkRequest.Builder(MyWorker::class.java,15,TimeUnit.MINUTES)
                .setInputData(data)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance().enqueue(periodicWorkRequest)

        /*
        Anda dapat membaca status secara live dengan menggunakan getWorkInfoByIdLiveData. Anda juga bisa memberikan aksi pada state tertentu dengan mengambil data state dan membandingkannya dengan konstanta yang bisa didapat di WorkInfo.State. Misalnya, pada kode di atas kita mengatur tombol Cancel task aktif jika task dalam state ENQUEUED.
         */
        WorkManager.getInstance().getWorkInfoByIdLiveData(periodicWorkRequest.id).observe(this@MainActivity,object: Observer<WorkInfo>{
            override fun onChanged(t: WorkInfo?) {
                val status = t?.state?.name
                textStatus.append("\n" + status)
                btnCancelTask.isEnabled = false
                if (t?.state == WorkInfo.State.ENQUEUED){
                    btnCancelTask.isEnabled = true
                }
            }

        })
    }

    private fun cancelPeriodicTask(){
        WorkManager.getInstance().cancelWorkById(periodicWorkRequest.id)
    }
    /*
    Kode di atas digunakan untuk membatalkan task berdasarkan id request. Selain menggunakan id, Anda juga bisa menambahkan tag pada request. Kelebihan dari penggunaan tag yaitu Anda bisa membatalkan task lebih dari satu task sekaligus seperti ini:
     */


    /*
Work Chaining
Selain menjalankan single task seperti pada latihan, Anda juga bisa membuat task-chaining, baik secara paralel maupun sekuensial. Berikut adalah contohnya:
 */
//    private fun getCurrentWorkChainning(){
//        WorkManager.getInstance()
//            .beginWith(workA1,workA2,work3)
//            .then(workB)
//            .then(workC1,workC2)
//            .enqueue()
//    }

}

/* Kesimpulan
    setRequiredNetworkType, ketika bernilai CONNECTED berarti dia harus terhubung ke koneksi internet, apa pun jenisnya. Bila kita ingin memasang ketentuan bahwa job hanya akan berjalan ketika perangkat terhubung ke network Wi-fi, maka kita perlu memberikan nilai UNMETERED.
    setRequiresDeviceIdle, menentukan apakah task akan dijalankan ketika perangkat dalam keadaan sedang digunakan atau tidak. Secara default, parameter ini bernilai false. Bila kita ingin task dijalankan ketika perangkat dalam kondisi tidak digunakan, maka kita beri nilai true.
 setRequiresCharging, menentukan apakah task akan dijalankan ketika baterai sedang diisi atau tidak. Nilai true akan mengindikasikan bahwa task hanya berjalan ketika baterai sedang diisi. Kondisi ini dapat digunakan bila task yang dijalankan akan memakan waktu yang lama.
 setRequiresStorageNotLow, menentukan apakah task yang dijalankan membutuhkan ruang storage yang tidak sedikit. Secara default, nilainya bersifat false.
 */

