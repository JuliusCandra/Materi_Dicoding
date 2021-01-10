package com.dev_candra.readwrite

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.OutputStreamWriter

internal object FileHelper {
    private val TAG = FileHelper::class.java.name

    // Menyimpan data yang bertipekan string kedalam sebuah
    // berkas pada internal storage
    
    fun writeToFile(fileModel: FileModel,context: Context){
        try{
            /*
            Dengan mengguanakn komponen OutputStreanWriter,
            Anda dapat menulis data kedalam berkas menggunakan stream.
            Pada proses inisasi OutputStreamWriter. Anda menggunakan metode openFileOutput
            untuk membuka ebrkas sesuai dengan namanya
            Jika berkas belum ada, maka berkas tersebut akan secara otomatis dibuatkan.
            Untuk menggunakan method openFileOutput() Anda harus mengetahui context aplikasi
            yang mengguanakannya. Oleh karena itu, dalam metode ini anda memberikan inputan paramter context
            Setelah berkas dibuka, Anda dapat menulis data menggunakan metode ouputStreamWriter.write(data)
             */
            val outputStreamWriter = OutputStreamWriter(context.openFileOutput(fileModel.filename.toString(),Context.MODE_PRIVATE))
            outputStreamWriter.write(fileModel.data.toString())

            // Tuutp berkas menggunakan baris kode dibawah
            outputStreamWriter.close()
        }catch (e: IOException){
            Log.d(TAG, "writeToFile: ",e)
        }
    }



    /*
   Pada metode readFromFile(), kita menggunakan komponen InputStreamReader. Namun logikanya masih sama. Data pada berkas akan dibaca menggunakan stream. Data pada tiap baris dalam berkas akan mampu diperoleh dengan menggunakan bufferedReader.
     */
    fun readFromFile(context: Context,filename: String):FileModel {
        val fileModel = FileModel()
        try {
            val inputStream = context.openFileInput(filename)
            if (inputStream != null){
                val receiverString = inputStream.bufferedReader().use(BufferedReader::readText)
                inputStream.close()
                fileModel.data = receiverString
                fileModel.filename = filename
            }
        }catch (e: FileNotFoundException){
            Log.e(TAG, "File not found: ",e )
        }catch (e: IOException){
            Log.e(TAG, "Can not read file: ",e)
        }
        return fileModel
    }

    /*
    Kesimpulan
    Pada kedua contoh di atas, metode writeToFile dan readFromFile berbentuk static. Karena sifatnya yang static, maka kedua metode tersebut dapat dipanggil tanpa menginisiasi kelas yang memilikinya.
     */
}