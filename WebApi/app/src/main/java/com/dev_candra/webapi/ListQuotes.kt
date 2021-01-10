package com.dev_candra.webapi

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_activity_list.*
import org.json.JSONArray
import java.lang.Exception

class ListQuotes : AppCompatActivity(){

    companion object{
        private val TAG = ListQuotes::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_activity_list)
        getListQuotes()
    }


    private fun getListQuotes(){
        setTollBar()

        showProgressBar(true)

        val client = AsyncHttpClient()
        val url = "https://quote-api.dicoding.dev/list"
        client.get(url,object : AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                // Jika koneksi berhasil
                showProgressBar(false)

                // patsing json
                if (responseBody != null) {
                    getParsingJson(responseBody)
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                // Jika koneksi Gagal
                showProgressBar(true)
                val errorMessage = when(statusCode){
                    401 -> "$statusCode : Bad Request"
                    403 -> "$statusCode : Forbidden"
                    404 -> "$statusCode : Not Found"
                    else -> "$statusCode : ${error?.message}"
                }
            }

        })
    }

    private fun setTollBar(){
        supportActionBar?.title = "Candra Julius Sinaga"
        supportActionBar?.subtitle = "List Quotes"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun showProgressBar(show: Boolean){
        if (show){
            progressBar1.visibility = View.VISIBLE
        }else{
            progressBar1.visibility = View.GONE}
        }

    private fun getParsingJson(responBody: ByteArray){
        val listQuotes = ArrayList<String>()

        val result = String(responBody)
        Log.d(TAG, "getParsingJson: $result")
        try {
            val jsonArray = JSONArray(result)

            for (i in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(i)
                val quotes = jsonObject.getString("en")
                val author = jsonObject.getString("author")
                listQuotes.add("\n$quotes\n - $author")
            }
            val adapter = ArrayAdapter(this@ListQuotes,android.R.layout.simple_list_item_1,listQuotes)
            listQuotes1.adapter = adapter
        }catch (e: Exception){
            Toast.makeText(this@ListQuotes,e.message,Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity(Intent(this@ListQuotes,MainActivity::class.java))
        finish()
        return super.onSupportNavigateUp()
    }
}