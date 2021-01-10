package com.dev_candra.viewmodel.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dev_candra.viewmodel.model.WeatherItems
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
import java.lang.Exception
import java.text.DecimalFormat

class MainViewModel : ViewModel(){

    // MutableLiveData adlaah data yang bisa diubah
    private val listWeathers = MutableLiveData<ArrayList<WeatherItems>>()

    fun setWeather(city : String){
        // request API
        val apiKey = "cfb986a544f79d4c3ffdf40668c3e2af"
        val url = "https://api.openweathermap.org/data/2.5/group?id=$city&units=metric&appid=${apiKey}"
        val client = AsyncHttpClient()
        client.get(url,object: AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?
            ) {
                // Your Code
                try {
                    val listItem1 = ArrayList<WeatherItems>()
                    // Cara untuk mendpatkan data sebuah array
                    val result = responseBody?.let { String(it) }
                    val responObject = JSONObject(result)
                    val listItems = responObject.getJSONArray("list")

                    for (i in 0 until listItems.length()){
                        val responItems = listItems.getJSONObject(i)
                        val listItems = WeatherItems()
                        listItems.id = responItems.getInt("id")
                        listItems.name = responItems.getString("name")
                        listItems.currentWeather = responItems.getJSONArray("weather").getJSONObject(0).getString("main")
                        listItems.description = responItems.getJSONArray("weather").getJSONObject(0).getString("description")
                        val tempKelvin = responItems.getJSONObject("main").getDouble("temp")
                        val tempCelcius = tempKelvin - 273
                        listItems.temperature = DecimalFormat("##.##").format(tempCelcius)
                        listItem1.add(listItems)
                    }
                    // Value atau nilai yang hanya dibaca saja
                    listWeathers.postValue(listItem1)

                }catch (e: Exception){
                    Log.d("onSuccess:",e.message.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<out Header>?,
                responseBody: ByteArray?,
                error: Throwable?
            ) {
                //Your Code
                Log.d("onFailure", error?.message.toString())
            }

        })
    }

    // Sedangkan LiveData adalah data yang tidak bisa diubah maskudnya data nya hanya bisa dibaca saja
    fun getWeathers(): LiveData<ArrayList<WeatherItems>>{
        return listWeathers
    }

    /*
    Kesimpulan
    setValue()
    Menetapkan sebuah nilai dari LiveData. Jika ada observer yang aktif, nilai akan dikirim kepada mereka. Metode ini harus dipanggil dari main thread.

    postValue()
    Posting tugas ke main thread untuk menetapkan nilai yang diberikan dari background thread, seperti melalui dalam kelas MainViewModel. Jika Anda memanggil metode ini beberapa kali sebelum main thread menjalankan tugas yang di-posting, hanya nilai terakhir yang akan dikirim.

    getValue()
    Mendapatkan nilai dari sebuah LiveData.
     */

    /*
    kesimpulan 2
    Intinya adalah setValue() bekerja di main thread dan postValue() bekerja di background thread.
     */

}