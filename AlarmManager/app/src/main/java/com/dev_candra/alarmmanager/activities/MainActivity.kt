package com.dev_candra.alarmmanager.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dev_candra.alarmmanager.R
import com.dev_candra.alarmmanager.kelas.DatePickerFragment
import com.dev_candra.alarmmanager.kelas.TimePickerFragment
import com.dev_candra.alarmmanager.receiver.AlarmService
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener,DatePickerFragment.DialogDateListener,TimePickerFragment.DialogTimeListener {

    private lateinit var alarmReceiver: AlarmService

    companion object{
        private const val DATE_PICKER_TAG = "DatePickerDialog"
        private const val TIME_PICKERONCE_TAG = "TimePickerDialog"
        private const val TIME_PICKER_REPEAT_TAG = "TimePickerRepeat"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clickedView()
        alarmReceiver = AlarmService()
    }


    private fun clickedView(){
        btn_once_date.setOnClickListener(this)
        btn_once_time.setOnClickListener(this)
        btn_set_once_alarm.setOnClickListener(this)
        btn_set_repeating_alarm.setOnClickListener(this)
        btn_repeating_time.setOnClickListener(this)
        btn_cancel_repeating_alarm.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            // Untuk mebuat aksi pada tanggal
            R.id.btn_once_date -> {
                val datePickerFragment = DatePickerFragment()
                datePickerFragment.show(supportFragmentManager, DATE_PICKER_TAG)
            }
            // Untuk membuat aksi pada waktu
            R.id.btn_once_time -> {
                val timePickerFragmentOne = TimePickerFragment()
                timePickerFragmentOne.show(supportFragmentManager, TIME_PICKERONCE_TAG)
            }
            // Membuat aksi pada tombol setOneTimeAlarm
            R.id.btn_set_once_alarm -> {
                val onceDate = tv_once_date.text.toString()
                val onceTime = tv_once_time.text.toString()
                val onceMessage = edt_once_message.text.toString()

                // Kode ini digunakan untuk memanggil metode setOneTimeAlarm yang berada  di dalam AlarmReceiver

                alarmReceiver.setOneTimeAlarm(this,AlarmService.TYPE_ONE_TIME
                ,onceDate
                ,onceTime,
                onceMessage)
            }

            R.id.btn_repeating_time -> {
                val timePickerFragment = TimePickerFragment()
                timePickerFragment.show(supportFragmentManager, TIME_PICKER_REPEAT_TAG)
            }

            R.id.btn_set_repeating_alarm -> {
                val repeatTime = tv_repeating_time.text.toString()
                val repeatingMessage = edt_repeating_message.text.toString()
                alarmReceiver.setRepeatingAlarm(this,AlarmService.TYPE_REPEATING,repeatTime,repeatingMessage)
            }

            R.id.btn_cancel_repeating_alarm -> {
                alarmReceiver.cancelAlarm(this,AlarmService.TYPE_REPEATING)
            }
        }
    }

    // Hasil dari implementasi DatePicker
    override fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int) {
        // Code
        val calendar = Calendar.getInstance()
        calendar.set(year,month,dayOfMonth)
        // Format yang menampilkan tanggal
        val dateFormat = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
        tv_once_date.text = dateFormat.format(calendar.time)
    }

    // Hasil dari implementasi TimePiicker..
    override fun onDialogTimeSet(tag: String?, hourOfDay: Int, minute: Int) {
        // Code
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay)
        calendar.set(Calendar.MINUTE,minute)
        // Format yang menampilkan waktu
        val dateFormat = SimpleDateFormat("HH:mm",Locale.getDefault())
        when(tag){
            TIME_PICKERONCE_TAG -> tv_once_time.text = dateFormat.format(calendar.time)
            TIME_PICKER_REPEAT_TAG -> tv_repeating_time.text = dateFormat.format(calendar.time)
            else -> {

            }
        }
    }
}