package com.dev_candra.shareprefrence

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dev_candra.shareprefrence.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mUserPreference: UserPreference
    private lateinit var binding: ActivityMainBinding
    private  var isPreferenceEmpty = false
    private lateinit var userModel: UserModel

    companion object{
        private const val REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mUserPreference = UserPreference(this)

        showExistingPreference()

        setToolbar()

        binding.btnSave.setOnClickListener(this)
    }

    private fun showExistingPreference() {
        // Your Code
        // START
        // Variabel userModel diatas kita inisiasikan dengan metode
        // getUser yang ada dikelas UserPreference.
        userModel = mUserPreference.getUser()

        // Terakhir kita memanggil metode populateView dengan
        // memasukkan variabel userModel
        // Setelah itu kita akan
        // mengatur TextView dengan memanggil variabel userModel yang tadi
        // sudah diinisiasikan.
        populateView(userModel)

        // Mengecek validasi view
        checkForm(userModel)
        // END
    }

    private fun checkForm(userModel: UserModel) {
        //Fungsi dari kode di atas yaitu untuk menentukan apakah sudah ada data yang tersimpan pada SharedPreference atau tidak, kemudian memasukkannya ke variabel isPreferenceEmpty. Variabel
        // ini dipakai ketika tombol Save diklik pada fungsi onClick berikut:
        when{
            userModel.name.toString().isEmpty() -> {
                binding.btnSave.text = getString(R.string.change)
                isPreferenceEmpty = false
            }
            else -> {
                binding.btnSave.text = getString(R.string.save)
                isPreferenceEmpty = true
            }
        }
    }

    private fun populateView(userModel: UserModel) {
        // pada baris kode ini secara otomatis akan memanggil default value jik data kosong
        binding.tvName.text = if (userModel.name.toString().isEmpty()) resources.getString(R.string.tidak_ada) else userModel.name
        binding.tvAge.text = if (userModel.age.toString().isEmpty()) resources.getString(R.string.tidak_ada) else userModel.age.toString()
        binding.tvIsLoveMu.text = if (userModel.isLove) resources.getString(R.string.ya) else resources.getString(R.string.tidak)
        binding.tvEmail.text = if (userModel.email.toString().isEmpty()) resources.getString(R.string.tidak_ada) else userModel.email
        binding.tvPhone.text = if  (userModel.phoneNumber.toString().isEmpty()) resources.getString(R.string.tidak_ada) else userModel.phoneNumber
    }

    private fun setToolbar(){
        supportActionBar?.title = resources.getString(R.string.nama_developer)
        supportActionBar?.subtitle = resources.getString(R.string.name_app)
    }

    override fun onClick(view: View?) {
        // Your Code
        if (view?.id == R.id.btn_save){
            val intent = Intent(this@MainActivity,UserPrefrenceActivity::class.java)
            when{
                /*
                Ketika isPreferenceEmpty bernilai true,  maka kita mengirim data ke FormUserPreferenceActivity dengan data TYPE_ADD,
                 */
                isPreferenceEmpty -> {
                    intent.putExtra(UserPrefrenceActivity.EXTRA_TYPE_FORM,UserPrefrenceActivity.TYPE_ADD)
                    intent.putExtra("USER",userModel)
                }

                else -> {
//                    sedangkan jika bernilai false akan mengirim data TYPE_EDIT.
                    intent.putExtra(UserPrefrenceActivity.EXTRA_TYPE_FORM,UserPrefrenceActivity.TYPE_EDIT)
                    intent.putExtra("USER",userModel)
                }
            }
            // berpindah activity dan mengirim sebuah nilai
            // REQUEST_CODE disini dipakai ketika mendapatkan hasil dari onActivityResult
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /*
        Kemudian diterima pada onActivityResult, dengan menyamakan resultCode-nya.
         */
        if (requestCode == REQUEST_CODE){
            if (resultCode == UserPrefrenceActivity.RESULT_CODE){
                userModel = data?.getParcelableExtra<UserModel>(UserPrefrenceActivity.EXTRA_RESULT) as UserModel
                populateView(userModel)
                checkForm(userModel)
            }
        }
    }
}