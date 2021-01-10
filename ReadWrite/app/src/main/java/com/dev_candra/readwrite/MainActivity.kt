package com.dev_candra.readwrite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.dev_candra.readwrite.databinding.ActivityMainBinding
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initToolbar()

        binding.buttonNew.setOnClickListener(this)
        binding.buttonOpen.setOnClickListener(this)
        binding.buttonSave.setOnClickListener(this)
    }


    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.button_new -> newFile()
            R.id.button_open -> openFile()
            R.id.button_save -> saveFile()
        }
    }

    private fun saveFile() {
        // Your Code
        when{
            // START Mengecek apakah title kosong dan kontent kosong
            binding.editTitle.text.toString().isEmpty() -> Toast.makeText(this,resources.getString(R.string.diisi),Toast.LENGTH_SHORT).show()
            binding.editFile.text.toString().isEmpty() -> Toast.makeText(this,resources.getString(R.string.kontent),Toast.LENGTH_SHORT).show()
            // END
            // Jika tidak kosong maka masukkan kedalam objek view
            // START
            else -> {
                val title = binding.editTitle.text.toString()
                val text = binding.editFile.text.toString()
                val fileModel = FileModel()
                fileModel.filename = title
                fileModel.data = text
                FileHelper.writeToFile(fileModel,this)
                Toast.makeText(this,resources.getString(R.string.saving),Toast.LENGTH_SHORT).show()
            }
            // END
        }
    }


    private fun openFile() {
        // Your Code
        /*
        Arraylist merupakan salah satu tipe collection yang mampu menampung banyak
        objek di dalamnya. Ini merupakan  bagian dari struktur data dasar pada pemrograman
        java/kotlin yang harus dikethaui sebelum menyelami proses pengembangan apliaksi lebih dalam
         */
        val arrayList = ArrayList<String>()
        // Start Kode yang digunakan untuk menampilkan data internal storeage
        /*

        Objek fileDir() akan secara otomatis memperoleh path internal storage
        aplikasi anda. Dengan menggunakan list(), Anda akan memperoleh semua nama berkas
        yang ada. Tipe berkas yang ditemukan ditambahkan kedalam objek arraylist
         */
        val path: File = filesDir
        Collections.addAll(arrayList, *path.list() as Array<String>)
        // End

        val items = arrayList.toTypedArray<CharSequence>()

        /*
        START
          Dengan memanfaatkan AlertDialog pada beberapa baris di atas, Anda dapat membuat daftar pilihan berkas sederhana. Setelah salah satu berkas pada daftar tersebut di pilih, maka aplikasi akan memanggil metode loadData().
         */
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.file_diinginkan))
        builder.setItems(items) { _, i -> loadData(items[i].toString())  }
        val alert = builder.create()
        alert.show()
        // END
    }

    private fun loadData(title: String){
        // START
        //  Dengan menggunakan method static pada kelas FileHelper, Kita dapat mengambil isi dari file yang dipilih dan memasukkannya ke dalam file model
        val fileModel = FileHelper.readFromFile(this,title)
        // END

        binding.editTitle.setText(fileModel.filename)
        binding.editFile.setText(fileModel.data)
        Toast.makeText(this,resources.getString(R.string.loading) + fileModel.filename + resources.getString(R.string.data),Toast.LENGTH_SHORT).show()
    }

    //START
    // Proses new/clear file disini sangat sederhana. Yaitu dengan mengosongkan title
    // konten dan kemudian menampilkan toast
    private fun newFile() {
        binding.editTitle.setText("")
        binding.editFile.setText("")
        Toast.makeText(this@MainActivity,resources.getString(R.string.Clearing),Toast.LENGTH_SHORT).show()
    }
    // END

    private fun initToolbar(){
        supportActionBar?.title = resources.getString(R.string.nama_developer)
        supportActionBar?.subtitle = resources.getString(R.string.app_name)
    }



//    Kesimpulan
    /*
    Kode dobawah adalah contoh tambahan bagaimana metode static dapat dipanggil
    tanpa menginisasi kelas yang memilikinya. Metode readFromFile dapat dijaankan tanpa menginisiasi kelas
    FileHelper
     */
//    val filemodel1 = FileHelper.readFromFile(this,title)

}
