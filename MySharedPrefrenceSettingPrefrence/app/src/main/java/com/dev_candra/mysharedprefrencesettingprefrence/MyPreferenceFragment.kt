package com.dev_candra.mysharedprefrencesettingprefrence

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import androidx.preference.CheckBoxPreference
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat

class MyPreferenceFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var age : String
    private lateinit var phone: String
    private lateinit var love: String


    private lateinit var namePreference: EditTextPreference
    private lateinit var emailPreference: EditTextPreference
    private lateinit var agePreference : EditTextPreference
    private lateinit var phonePreference : EditTextPreference
    private lateinit var isLovePreference : CheckBoxPreference

    companion object{
        private const val DEFAULT_VALUE = "Tidak Ada"
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        init()
        setSummaries()
    }

    // method ini digunakan untuk memanggil komponen seperti EditTextPreference dan CheckBoxPreference
    private fun init(){
        name = resources.getString(R.string.key_name)
        email = resources.getString(R.string.key_email)
        age = resources.getString(R.string.key_age)
        phone = resources.getString(R.string.key_phone)
        love = resources.getString(R.string.key_love)

        namePreference = findPreference<EditTextPreference>(name)as EditTextPreference
        emailPreference = findPreference<EditTextPreference>(email)as EditTextPreference
        agePreference = findPreference<EditTextPreference>(age)as EditTextPreference
        phonePreference = findPreference<EditTextPreference>(phone)as EditTextPreference
        isLovePreference = findPreference<EditTextPreference>(love)as CheckBoxPreference

    }

    // lalu method dibawah ini digunakan untuk merubah summarynya.
    // Tulis method ini di onCreatePreferences supaya terpanggil pada saat
    // pertama kali aplikasi dibuka
    private fun setSummaries(){
        val sh = preferenceManager.sharedPreferences
        namePreference.summary = sh.getString(name, DEFAULT_VALUE)
        emailPreference.summary = sh.getString(email, DEFAULT_VALUE)
        agePreference.summary = sh.getString(age, DEFAULT_VALUE)
        phonePreference.summary = sh.getString(phone, DEFAULT_VALUE)
        isLovePreference.isChecked = sh.getBoolean(love, false)
        // setSummary berguna untuk mengubah value dari EditTextPreference menjadi nilai
    }

    // Kode ini digunakan untuk me register ketika aplikasi dibuka
    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    // kode ini digunakan untuk me unregister ketika aplikasi ditutup
    // Hal ini supaya listener tidak berjalan tersu menerus dan menyebabkan memory leak
    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    /*
    Kode di atas digunakan untuk mengecek apakah terjadi perubahan pada data yang tersimpan. Jika terdapat value yang berubah maka akan memanggil listener onSharedPreferenceChanged. Untuk mendapatkan value tersebut, lakukan validasi terlebih dahulu. Dengan mencocokkan key mana yang berubah, hasilnya juga akan berubah.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        // Your Code
       if (key == name){
           namePreference.summary = sharedPreferences.getString(name, DEFAULT_VALUE)
       }

        if (key == email){
            emailPreference.summary = sharedPreferences.getString(email, DEFAULT_VALUE)
        }

        if (key == age){
            agePreference.summary = sharedPreferences.getString(age, DEFAULT_VALUE)
        }

        if (key == phone){
            phonePreference.summary = sharedPreferences.getString(phone, DEFAULT_VALUE)
        }

        if (key == love){
                isLovePreference.isChecked = sharedPreferences.getBoolean(love,false)
        }

    }

}