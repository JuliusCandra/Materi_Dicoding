package com.dev_candra.shareprefrence

import android.content.Context

internal class UserPreference (context: Context){
    companion object{
        //START  membuat sebuah key
        private const val PREFS_NAME = "user_pref"
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val AGE = "age"
        private const val PHONE_NUMBER = "phone"
        private const val LOVE_MU = "islove"
        // END membuat sebuah key
    }

    // Code untuk objek shared prefernce yang akan di kirim ke activity
    private val preference = context.getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE)


    // setter dari objek shared preference
    fun setUser(value: UserModel){
        // disini data akan disimpan di userpreference
        val editor = preference.edit()
        editor.putString(NAME,value.name)
        editor.putString(EMAIL,value.email)
        editor.putInt(AGE,value.age)
        editor.putString(PHONE_NUMBER,value.phoneNumber)
        editor.putBoolean(LOVE_MU,value.isLove)
        // dengan menggunakan metode apply maka data akan disimpan di metode userprefernce
        editor.apply()
    }

    // getter dari objek preference
    fun getUser(): UserModel{
        // Untuk menyimpan sebuah nilai dari default key
        val model = UserModel()
        model.name = preference.getString(NAME,"")
        model.email = preference.getString(EMAIL,"")
        model.age = preference.getInt(AGE,0)
        model.phoneNumber = preference.getString(PHONE_NUMBER,"")
        model.isLove = preference.getBoolean(LOVE_MU,false)
        // mengembalikan nilai dari default
        return model
    }

}