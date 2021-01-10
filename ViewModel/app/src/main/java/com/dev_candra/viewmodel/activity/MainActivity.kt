package com.dev_candra.viewmodel.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.res.TypedArrayUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dev_candra.viewmodel.R
import com.dev_candra.viewmodel.adapter.AdapterList
import com.dev_candra.viewmodel.model.WeatherItems
import com.dev_candra.viewmodel.viewmodel.MainViewModel
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.lang.Exception
import java.text.DecimalFormat

class MainActivity : AppCompatActivity(),View.OnClickListener {


    companion object {
        private lateinit var adapter : AdapterList
        private val TAG = MainActivity::class.java.simpleName
        private lateinit var mainView: MainViewModel
        private val listWeatherItems = ArrayList<WeatherItems>()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = AdapterList()
        // kode yang menyambungkan activity dengan mainViewModel
        mainView = ViewModelProvider(this,ViewModelProvider.NewInstanceFactory()).get(MainViewModel::class.java)

        setRecylerView()
        setOnClickView()
        setToolbar()
    }

    private fun setOnClickView(){
        btnCity.setOnClickListener(this)

        /*
        Jika Anda perhatikan kode di atas, weatherItems akan selalu diperbarui secara realtime sesuai dengan perubahan yang ada di kelas ViewModel.
        Namun jika getWeather tidak dipanggil saat melakukan observe getWeathers() maka nilai weatherItems juga tidak akan ada perubahan.
        Jadi cara mendapatkan value dari sebuah LiveData harus dilakukan dengan cara meng-observe LiveData tersebut. Dan proses ini dilakukan secara asynchronous.
         */
        mainView.getWeathers().observe(this, Observer { weatherItems ->
            if (weatherItems != null) {
                adapter.setData(weatherItems)
                showProgressBar(false)
            }
        })
    }

    override fun onClick(v: View?) {
        // Your Code
        when(v?.id){
            R.id.btnCity -> {
                val cities = editCity.text.toString()
                if (cities.isEmpty()) return
                showProgressBar(true)
                mainView.setWeather(cities)
            }
        }
    }




    private fun showProgressBar(show: Boolean){
        if (show){
            progressBar.visibility = View.VISIBLE
        }else{
            progressBar.visibility = View.GONE
        }
    }

    private fun setRecylerView(){
        adapter.notifyDataSetChanged()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setToolbar(){
        supportActionBar?.title = "Candra Julius Sinaga"
        supportActionBar?.subtitle = getString(R.string.app_name)
    }


}