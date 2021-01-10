package com.dev_candra.myperloaddata.database

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns._ID
import com.dev_candra.myperloaddata.database.DatabaseContract.MahasiswaColumns.Companion.NAMA
import com.dev_candra.myperloaddata.database.DatabaseContract.MahasiswaColumns.Companion.NIM
import com.dev_candra.myperloaddata.database.DatabaseContract.TABLE_NAME
import com.dev_candra.myperloaddata.model.MahasiswaModel

class MahasiswaHelper(context: Context) {

    private val dataBaseHelper: DatabaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    companion object {
        private var INSTANCE: MahasiswaHelper? = null

        fun getInstance(context: Context): MahasiswaHelper {
            if (INSTANCE == null) {
                synchronized(SQLiteOpenHelper::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = MahasiswaHelper(context)
                    }
                }
            }
            return INSTANCE as MahasiswaHelper
        }
    }

    /*
    START
    Dengan memanggil kode di bawah maka database siap untuk menerima transaction
     */
    fun beginTransaction() {
        database.beginTransaction()
    }
    // END

    /*
    Metode di atas akan menandakan bahwa semua data berhasil telah berhasil dimasukkan. Kita perlu memastikan untuk menjalankan setTransactionSuccesful() ketika proses transactional berhasil diselesaikan. Bila Anda ingin membatalkan proses transactional tersebut, misalnya karena eror,  maka kita tak perlu menjalankan setTransactionSuccessful().
     */
    fun setTransactionSuccess() {
        database.setTransactionSuccessful()
    }

    /*
Kode di atas menunjukkan bahwa transaction selesai.
     */
    fun endTransaction() {
        database.endTransaction()
    }

    /*
    Kemudian dengan menggunakan looping, satu per satu data dimasukkan ke dalam database dengan menggunakan metode insertTransaction().
     */
    fun insertTransaction(mahasiswaModel: MahasiswaModel) {
        val sql = ("INSERT INTO $TABLE_NAME ($NAMA, $NIM) VALUES (?, ?)")
        val stmt = database.compileStatement(sql)
        stmt.bindString(1, mahasiswaModel.name)
        stmt.bindString(2, mahasiswaModel.nim)
        stmt.execute()
        stmt.clearBindings()
    }
    // END

    @Throws(SQLException::class)
    fun open() {
        database = dataBaseHelper.writableDatabase
    }

    fun close() {
        dataBaseHelper.close()

        if (database.isOpen)
            database.close()
    }

    fun getAllData(): ArrayList<MahasiswaModel> {
        val cursor = database.query(TABLE_NAME, null, null, null, null, null, "$_ID ASC", null)
        cursor.moveToFirst()
        val arrayList = ArrayList<MahasiswaModel>()
        var mahasiswaModel: MahasiswaModel
        if (cursor.count > 0) {
            do {
                mahasiswaModel = MahasiswaModel()
                mahasiswaModel.id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID))
                mahasiswaModel.name = cursor.getString(cursor.getColumnIndexOrThrow(NAMA))
                mahasiswaModel.nim = cursor.getString(cursor.getColumnIndexOrThrow(NIM))

                arrayList.add(mahasiswaModel)
                cursor.moveToNext()
            } while (!cursor.isAfterLast)
        }
        cursor.close()
        return arrayList
    }

    fun insert(mahasiswaModel: MahasiswaModel): Long {
        val initialValues = ContentValues()
        initialValues.put(NAMA, mahasiswaModel.name)
        initialValues.put(NIM, mahasiswaModel.nim)
        return database.insert(TABLE_NAME, null, initialValues)
    }

    fun getDataByName(nama: String): ArrayList<MahasiswaModel> {
        val cursor = database.query(
            TABLE_NAME,
            null,
            "$NAMA LIKE ?",
            arrayOf(nama),
            null,
            null,
            "$_ID ASC",
            null
        )
        cursor.moveToFirst()
        val arrayList = ArrayList<MahasiswaModel>()
        var mahasiswaModel: MahasiswaModel
        if (cursor.count > 0) {
            do {
                mahasiswaModel = MahasiswaModel()
                mahasiswaModel.id = cursor.getInt(cursor.getColumnIndexOrThrow(_ID))
                mahasiswaModel.name = cursor.getString(cursor.getColumnIndexOrThrow(NAMA))
                mahasiswaModel.nim = cursor.getString(cursor.getColumnIndexOrThrow(NIM))

                arrayList.add(mahasiswaModel)
                cursor.moveToNext()

            } while (!cursor.isAfterLast)
        }
        cursor.close()
        return arrayList
    }
}
