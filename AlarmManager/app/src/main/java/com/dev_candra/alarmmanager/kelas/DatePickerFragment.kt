package com.dev_candra.alarmmanager.kelas

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class DatePickerFragment : DialogFragment(),DatePickerDialog.OnDateSetListener{

    private var mListener: DialogDateListener? = null

    /*
    Fungsi onAttach() hanya sekali dipanggil dalam fragement dan berfungsi
    untuk mengkaitkan dengan activity pemanggil,
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context != null){
            mListener = context as DialogDateListener?
        }
    }

    /*
    sedangkan onDetach() hanya di panggil sebelum fragment tidak lagi dikatikan dengan
    acitivity
     */
    override fun onDetach() {
        super.onDetach()
        if (mListener != null){
            mListener = null
        }
    }

    interface DialogDateListener {
        fun onDialogDateSet(tag: String?, year: Int, month: Int, dayOfMonth: Int)
    }

    /*
    Fungsi onDataSet akan dipanggil ketika kita memilih tanggal yang kita inginkan
    Kemudian setelah tanggal di pilih maka variable tanggal bulan dan tahun akan dikirim ke
    MainActivity menggunakan bantuan interface DialogDateListener
     */
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        mListener?.onDialogDateSet(tag,year,month,dayOfMonth)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val date = calendar.get(Calendar.DATE)

        return DatePickerDialog(activity as Context, this,year,month,date)
    }

}