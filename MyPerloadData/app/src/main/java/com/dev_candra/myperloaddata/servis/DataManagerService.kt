package com.dev_candra.myperloaddata.servis

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import com.dev_candra.myperloaddata.R
import com.dev_candra.myperloaddata.database.MahasiswaHelper
import com.dev_candra.myperloaddata.model.MahasiswaModel
import com.dev_candra.myperloaddata.pref.AppPreference
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.coroutines.CoroutineContext

class DataManagerService : Service(), CoroutineScope {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private var mActivityMessenger: Messenger? = null

    companion object {
        const val PREPARATION_MESSAGE = 0
        const val UPDATE_MESSAGE = 1
        const val SUCCESS_MESSAGE = 2
        const val FAILED_MESSAGE = 3
        const val CANCEL_MESSAGE = 4
        const val ACTIVITY_HANDLER = "activity_handler"
        private const val MAX_PROGRESS = 100.0
        private  var TAG = DataManagerService::class.java.simpleName
    }

    override fun onCreate() {
        super.onCreate()
        /*
        Pada aplikasi MyPreLoadData ini, Anda menerapkan service pada aplikasi tersebut. Service ini berfungsi untuk memanggil kelas LoadDataAsync.
        Ketika kelas service ini terbentuk, maka akan dijalankan background process untuk mengambil data dari database.
         */

        job = Job() //create the Job

        Log.d(Companion.TAG, "onCreate: ")
    }

    /*
    Ketika semua ikatan sudah di lepas maka ondestroy akan secara otomatis dipanggil
     */
    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // cancel the Job

        Log.d(Companion.TAG, "onDestroy: ")
    }

    /*
    Method yang akan dipanggil ketika service diikatkan ke activity
    Kemudian pada bagian onBinder atau ketika service sudah terikat dengan Activity pemanggil, ia akan menjalankan loadData.
    */
    override fun onBind(intent: Intent): IBinder? {

        mActivityMessenger = intent.getParcelableExtra(ACTIVITY_HANDLER)

        loadDataAsync()

        return mActivityMessenger.let { it?.binder }
    }

    /*
    Ketika mengirim Message atau pesan ke Messager, yang perlu dimasukkan adalah status dari pesan tersebut. Seperti di atas, kelas service akan mengirimkan pesan ketika terjadi persiapan sebelum background process bekerja. Selain itu Anda juga bisa memasukkan data dari service menuju Activity.
     */


    /*
    Method yang akan dipanggil ketika service dilepas dari activity
    Bagian onUnBind berfungsi untuk melepaskan ikatan kelas DataManagerService dari Activity pemanggil.
     */
    override fun onUnbind(intent: Intent): Boolean {
        Log.d(Companion.TAG, "onUnbind: ")
        job.cancel()
        return super.onUnbind(intent)
    }

    /*
    Method yang akan dipanggil ketika service diikatkan kembali
     */
    override fun onRebind(intent: Intent) {
        super.onRebind(intent)
        Log.d(Companion.TAG, "onRebind: ")
    }


    private fun loadDataAsync() {
        sendMessage(PREPARATION_MESSAGE)
        job = launch {
            val isInsertSuccess = async(Dispatchers.IO) {
                getData()
            }
            if (isInsertSuccess.await()){
                sendMessage(SUCCESS_MESSAGE)
            } else {
                sendMessage(FAILED_MESSAGE)
            }
        }
        job.start()
    }

    /*
    Start
    Lalu untuk menghubungkan kelas service dengan Activity menggunakan bantuan obyek Message. Coba lihat Message bekerja di kelas DataManagerService:
     */
    private fun sendMessage(messageStatus: Int) {
        val message = Message.obtain(null, messageStatus)
        try {
            mActivityMessenger?.send(message)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }
    /*
    Dengan bantuan Message, Anda bisa meneruskan kejadian yang ada di DataManagerService menuju Activity.
     */

    private fun getData(): Boolean {
        val mahasiswaHelper = MahasiswaHelper.getInstance(applicationContext)
        val appPreference = AppPreference(applicationContext)

        val firstRun = appPreference.firstRun as Boolean
        /*
         * Jika first run true maka melakukan proses pre load,
         * Jika first run false maka akan langsung menuju home
         */
        if (firstRun) {
            /*
            Load raw data dari file txt ke dalam array model mahasiswa
            */
            /*
            Start
            Kode di atas akan memasang nilai progress (kemajuan) pada 30%. Kemudian progress akan dihitung dengan cara menemukan nilai progressDiff yang merepresentasikan progress memasukkan data per modelnya.
Sebagai contoh, anggap mahasiswaModels berjumlah 5. Maka setiap satu model yang sudah ditambahkan, progress akan bertambah sebesar 10%.
             */
            val mahasiswaModels = preLoadRaw()

            mahasiswaHelper.open()

            var progress = 30.0
            publishProgress(progress.toInt())
            val progressMaxInsert = 80.0
            val progressDiff = (progressMaxInsert - progress) / mahasiswaModels.size
            // END

            var isInsertSuccess: Boolean

            /*
            Gunakan ini untuk insert query dengan menggunakan standar query
             */

            // START
            // Kode ini akan menjalankan proses transaction
            try {

                mahasiswaHelper.beginTransaction()

                loop@ for (model in mahasiswaModels) {
                    //Jika service atau activity dalam keadaan destroy maka akan menghentikan perulangan
                    when {
                        job.isCancelled -> break@loop
                        else -> {
                            mahasiswaHelper.insertTransaction(model)
                            /*
                            Setiap proses memasukkan data selesai, maka progress bar perlu diperbarui dengan cara memanggil metode publishProgress..
                             */
                            progress += progressDiff
                            publishProgress(progress.toInt())
                        }
                    }
                }

                when {
                    job.isCancelled -> {
                        isInsertSuccess = false
                        appPreference.firstRun = true
                        sendMessage(CANCEL_MESSAGE)
                    }
                    else -> {
                        mahasiswaHelper.setTransactionSuccess()
                        isInsertSuccess = true
                        appPreference.firstRun = false
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "doInBackground: Exception")
                isInsertSuccess = false
            } finally {
                mahasiswaHelper.endTransaction()
            }
            // END

            // Close helper ketika proses query sudah selesai
            mahasiswaHelper.close()

            publishProgress(MAX_PROGRESS.toInt())

            return isInsertSuccess

        } else {
            try {
                synchronized(this) {
                    publishProgress(50)
                    publishProgress(MAX_PROGRESS.toInt())
                    return true
                }
            } catch (e: Exception) {
                return false
            }

        }
    }

    /*
    Menggunakan bantuan Bundle untuk mengirim value dari service menuju Activity.
     */
    private fun publishProgress(progress: Int) {
        try {
            val message = Message.obtain(null, UPDATE_MESSAGE)
            val bundle = Bundle()
            bundle.putLong("KEY_PROGRESS", progress.toLong())
            message.data = bundle
            mActivityMessenger?.send(message)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }


/*
Kode di bawah mengambil data mentah dari data_mahasiswa. Kemudian tiap baris dari data tersebut akan di-parse.
 */
    private fun preLoadRaw(): ArrayList<MahasiswaModel> {
        val mahasiswaModels = ArrayList<MahasiswaModel>()
        var line: String?
        val reader: BufferedReader
        try {
            val rawText = resources.openRawResource(R.raw.data_mahasiswa)

            reader = BufferedReader(InputStreamReader(rawText))
            do {
                /*START
                Kode ini akan membaca teks tiap baris dan membaginya berdasarkan "\t" atau tab. Bila kita melihat data mentahnya, maka nama dan nilai id dipisah menggunakan “\t” atau tab.
                 */
                line = reader.readLine()
                val splitstr = line.split("\t".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                // END

                /*
                Start
                Kode dibawah ini akan mengambil data nama id dari objek
                 */
                val mahasiswaModel = MahasiswaModel()
                mahasiswaModel.name = splitstr[0]
                mahasiswaModel.nim = splitstr[1]
                mahasiswaModels.add(mahasiswaModel)
            } while (line != null)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return mahasiswaModels
    }
}
