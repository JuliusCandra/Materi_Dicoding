package com.dev_candra.stacknotification

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.dev_candra.stacknotification.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    /*
    Variabel idNotification adalah indeks yang akan kita gunakan untuk mengakses list stackNotif. Semua notifikasi yang kita kirimkan akan kita masukkan ke dalam variabel stackNotif tersebut.

Mengapa perlu disimpan? Tujuannya agara kita tahu berapa jumlah notifikasi yang kita tampilkan. Jumlah tersebut akan kita gunakan sebagai parameter untuk menggunakan stack notifikasi.
     */

    companion object {
        private const val CHANNEL_NAME = "dicoding channel"
        private const val GROUP_KEY_EMAILS = "group_key_emails"
        private const val NOTIFICATION_REQUEST_CODE = 200
        private const val MAX_NOTIFICATION = 2
        private const val CHANNEL_ID = "channel_01"
    }
    private var idNotification = 0
    private val stackNotif = ArrayList<NotificationItem>()
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("ServiceCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSend.setOnClickListener {
            val sender = binding.edtSender.text.toString()
            val message = binding.edtMessage.text.toString()
            if (sender.isEmpty() || message.isEmpty()) {
                Toast.makeText(this@MainActivity, "Data harus diisi", Toast.LENGTH_SHORT).show()
            } else {
                /*
                OK, kita lanjutkan pada implementasi OnClick dari btnKirim. Lihat percabangan di dalam else yang isinya kode berikut:
                 */
                stackNotif.add(NotificationItem(idNotification, sender, message))
                sendNotif()
                idNotification++
                /*
                Di sini kita membuat obyek baru NotificationItem yang kemudian kita tambahkan pada list stackNotif. Setelah itu kita jalankan metode sendNotif(). Proses diakhiri dengan menaikkan variable idNotif dengan menggunakan kode idNotif++.

Menaikkan nilai idNotif sangatlah penting karena dia digunakan sebagai indeks untuk mengakses item dari stackNotif yang terakhir kali dimasukkan.
                 */
                binding.edtSender.setText("")
                binding.edtMessage.setText("")
                //tutup keyboard ketika tombol diklik
                val methodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                methodManager.hideSoftInputFromWindow(edtMessage.windowToken, 0)
            }
        }
    }

    private fun sendNotif() {
        /*
        Kita dapat langsung mendapatkan komponen manager dari notification dengan kode di atas. Dan ketika ingin melakukan update tinggal panggil mNotificationManager.notify().
         */
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val mBuilder: NotificationCompat.Builder

        //Melakukan pengecekan jika idNotification lebih kecil dari Max Notif
        val channelID = "channel_01"
        /*
        Di dalam metode sendNotif() terdapat percabangan if yang menghitung apakah index idNotif sudah melewati nilai dari maxNotif. Jika belum, maka notifikasi yang akan ditampilkan adalah notifikasi biasa yang ada pada baris ini:
         */
        if (idNotification < MAX_NOTIFICATION) {
            mBuilder = NotificationCompat.Builder(this, channelID)
                    .setContentTitle("New Email from " + stackNotif[idNotification].sender)
                    .setContentText(stackNotif[idNotification].message)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setLargeIcon(largeIcon)
                    .setGroup(GROUP_KEY_EMAILS)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
            /*
            Dan ketika indeks idNotif sudah lebih besar dari nilai maxNotif , maka notifikasinya akan dikelompokkan. Hanya beberapa notifikasi terakhir yang akan ditampilkan. Perhatikanlah kode berikut ini:
             */
        } else {
            val inboxStyle = NotificationCompat.InboxStyle()
                    /*
                    Text yang akan ditampilkan ada pada obyek InboxStyle. Perhatikan pada kode addLine. Pada kode tersebut ada 2 buah addLine. Yang pertama:
                     */
                    .addLine("New Email from " + stackNotif[idNotification].sender)
                    /*
                    Kedua addLine di atas mengindikasikan bahwa hanya 2 baris yang akan ditampilkan pada kelompok notifikasi. Baris pertama adalah data sender dari stackNotif, di mana nilai indeksnya diperoleh dari idNotif. Sementara itu, baris kedua adalah sender dari stackNotif di mana indeksnya diperoleh dari operasi idNotif minus satu.

Jika kita ingin menampilkan lebih dari 2 notifikasi, cukup tambahkan addLine lagi di bawahnya.
                     */
                    .addLine("New Email from " + stackNotif[idNotification - 1].sender)
                    .setBigContentTitle("$idNotification new emails")
                    .setSummaryText("mail@dicoding")
            mBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("$idNotification new emails")
                    .setContentText("mail@dicoding.com")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setGroup(GROUP_KEY_EMAILS)
                    .setGroupSummary(true)
                    .setContentIntent(pendingIntent)
                    .setStyle(inboxStyle)
                    .setAutoCancel(true)
            /*
            Yang harus diperhatikan adalah metode setGroup() dan setGroupSummary(true). Kedua metode inilah yang akan menjadikan notifikasi menjadi sebuah bundle notification.
             */
        }
        /*
        Untuk android Oreo ke atas perlu menambahkan notification channel
        Materi ini akan dibahas lebih lanjut di modul extended
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* Create or update. */
            val channel = NotificationChannel(CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT)
            mBuilder.setChannelId(CHANNEL_ID)
            mNotificationManager.createNotificationChannel(channel)
        }
        val notification = mBuilder.build()
        /*
        Yang mana merupakan kode untuk menampilkan notifikasi.
         */
        mNotificationManager.notify(idNotification, notification)
    }

    /*
    Metode ini akan dijalankan ketika ada intent baru (pending intent) yang dikirimkan ke dalam activity. Dengan menjalankan stackNotif.clear(); dan idNotif=0; maka kita menghapus semua data pada list stackNotif dan mengembalikan indeks idNotif menjadi 0. Alhasil, semuanya di-reset kembali ketika user menekan notifikasi yang ada.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        stackNotif.clear()
        idNotification = 0
    }
}