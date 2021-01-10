package com.dev_candra.webapi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception


/*
POST : Untuk membuat data (Create)
GET : Untuk membaca data (Read)
PUT : Untuk mengubah data (Update)
DELETE : Untuk menghapus data (Delete)


String : teks (dibungkus dengan “ “)
Integer : angka bulat (misal: 0, 3, 40)
Double : angka desimal (misal 0.2,  3.14, 40.0)
Boolean : true/false
Array : [“value1”, “value2”]
Object : { “key” : “value” }
Null : null
 */

class MainActivity : AppCompatActivity() {

    companion object{
        private val TAG = MainActivity::class.java.simpleName
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getRandomQuote()
        btnAllQuotes.setOnClickListener {
            val intent = Intent(this@MainActivity,ListQuotes::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun getRandomQuote(){
        setToolbar()
        progressBar.visibility = View.VISIBLE
        // mengambil data berdasarkan client loopj
        val client = AsyncHttpClient()
        // menyalin urlnya
        val url = "https://quote-api.dicoding.dev/random"
        client.get(url,object : AsyncHttpResponseHandler(){
            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
                // Jika koneksi berhasil
                showProgressbar(true)
                // mengubah / mnengconvert byte array ke string 
                    val result = responseBody?.let { String(it) }
                Log.d(TAG, "onSuccess: $result")
                try{
                    // Setetlah diubah baru lihat json nya pertama
                    // mengamvil data json object {}
                    val responObject = JSONObject(result)
                    // ambil satu datanya berdasarkan kety
                    val quote = responObject.getString("en")
                    val author = responObject.getString("author")

                    tvQuote.text = quote
                    tvAuthor.text = author

                }catch (e: Exception){
                    Toast.makeText(this@MainActivity,e.message,Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
                // Jika koneksi gagal
                showProgressbar(false)
                //mengambil semua koneksi server
                val errorMessage = when(statusCode){
                    401 ->  "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
                Toast.makeText(this@MainActivity,"$errorMessage",Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun setToolbar(){
        supportActionBar?.title = "Candra Julius Sinaga"
        supportActionBar?.subtitle = "My Quote"
    }

    private fun showProgressbar(show : Boolean){
        if (show){
            progressBar.visibility = View.GONE
        }else{
            progressBar.visibility = View.VISIBLE
        }
    }
}


// Kesimpulan
/*

        // Mengambil data dari JSON Field untuk mendapatkan tipe data yang diambil
//        val jsonObject = JSONObject(response)
//        val page = jsonObject.getInt("page")

        // Untuk mengambil data yang bertanda [] kurung siku menggunakan getJSONArray
        // Contoh
//        val jsonObhect = JSONObject(response)
//        val dataArray = jsonObhect.getJSONArray("data")

        // Untuk mengambil data yang bertanda {} (kurung siku), Anda dapa menggunakan getJSONObject(index)
//        val jsonObject = JSONObject(response)
//        val dataArray = jsonObject.getJSONArray("data")
//        val dataObject1 = dataArray.getJSONObject(0)
//        val email = dataObject1.getString("email")
//        val last_name = dataObject1.getString("last_name")


        // Contoh menggunakan get
//        val client = AsyncHttpClient()
//        val url = "https://reqres.in/api/users?page=1"
//        client.get(url,object: AsyncHttpResponseHandler(){
//            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
//                // called when response HTTP status is "200 OK"
//            }
//
//            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
//                TODO("Not yet implemented")
//                // called when response HTTP status is "4XX"(eg.401,403,404)
//            }
//
//        })
//
//        // Gunanya method post() untuk mengirim data dengan parameter ke server
//        // Contoh menggunakan method post() dan menambahkan RequestParams untuk memasukkan parameter
//        val client1  = AsyncHttpClient()
//        val params = RequestParams()
//        params.put("name","Candra")
//        params.put("job","Mahasiswa")
//        val url1 = "https://reqres.in/api/users"
//        client1.post(url1,object: AsyncHttpResponseHandler() {
//            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
//                // called when response HTTP status is "200" OK
//            }
//
//            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
//                // called when response HTTP status is "4XX"(eg. 401,402,403,404)
//            }
//        })
//
//
//        // Contoh menggunakan put() untuk update
//        val client3 = AsyncHttpClient()
//        val params1 = RequestParams()
//        params1.put("name","candra")
//        params1.put("job","mahasiswa")
//        val url3 = "https://reqres.in/api/users"
//        client.put(url,object : AsyncHttpResponseHandler(){
//            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
//                // called when response  HTTP status is "200 OK"
//            }
//
//            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
//                // called when response HTTP status is "4XX"(eg. 401,403,404)
//            }
//        })
//
//        // Contoh untuk menggunakan method delete() untuk menghapus data
//        val client4 = AsyncHttpClient()
//        val param2 = RequestParams()
//        param2.put("id","21")
//        val url4 = "https://reqres.in/api/users"
//        client4.delete(url,object : AsyncHttpResponseHandler(){
//            override fun onSuccess(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?) {
//                // called when response HTTP status is "200 OK"
//            }
//
//            override fun onFailure(statusCode: Int, headers: Array<out Header>?, responseBody: ByteArray?, error: Throwable?) {
//               // called when response HTTP status is "4XX"(eg. 401,403,404)
//            }
//
//        })

 */