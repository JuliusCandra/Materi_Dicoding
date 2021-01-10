package com.dev_candra.shareprefrence

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import com.dev_candra.shareprefrence.databinding.ActivityUserPrefrenceBinding
import kotlinx.android.synthetic.main.activity_main.*

class UserPrefrenceActivity : AppCompatActivity(), View.OnClickListener {

    companion object{
        const val EXTRA_TYPE_FORM = "extra_type_form"
        const val EXTRA_RESULT = "extra_result"
        const val RESULT_CODE = 101

        const val TYPE_ADD = 1
        const val TYPE_EDIT = 2

        private const val FIELD_REQUIRED = "Field tidak boleh kosong"
        private const val FIELD_DIGIT_ONLY = "Hanya boleh berisi numerik"
        private const val FIELD_IS_NOT_VALID = "Email tidak valid"
    }

    private lateinit var userModel : UserModel
    private lateinit var binding: ActivityUserPrefrenceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserPrefrenceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnSave.setOnClickListener(this)


        userModel = intent.getParcelableExtra<UserModel>("USER") as UserModel
        val formType = intent.getIntExtra(EXTRA_TYPE_FORM,0)

        var actionBarTitle = ""
        var btnTItle = ""

        when (formType){
            TYPE_ADD -> {
                actionBarTitle = "Tambah Baru"
                btnTItle = "Simpan"
            }
            TYPE_EDIT -> {
                actionBarTitle = "Ubah"
                btnTItle = "Update"
                showPreferenceInForm()
            }
        }

        setInitToolbar(actionBarTitle)
        binding.btnSave.text = btnTItle

    }

    private fun showPreferenceInForm() {
        binding.edtName.setText(userModel.name)
        binding.edtEmail.setText(userModel.email)
        binding.edtAge.setText(userModel.age.toString())
        binding.edtPhone.setText(userModel.phoneNumber)

        if (userModel.isLove){
            binding.rbYes.isChecked = true
        }else{
            binding.rbNo.isChecked = true
        }
    }

    override fun onClick(p0: View?) {
        // Your Code
       when(p0?.id){
           R.id.btn_save -> btnSave()
       }
    }

    private fun saveUser(name: String, email: String, age: String, phoneNo: String, love: Boolean) {
        val userPrefrence = UserPreference(this)
        userModel.name = name
        userModel.email = email
        userModel.age = Integer.parseInt(age)
        userModel.phoneNumber = phoneNo
        userModel.isLove = love

        userPrefrence.setUser(userModel)
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setInitToolbar(subTitle: String){
        supportActionBar?.title = resources.getString(R.string.nama_developer)
        supportActionBar?.subtitle = subTitle
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun btnSave(){
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val age = binding.edtAge.text.toString()
        val phoneNo = binding.edtPhone.text.toString()
        val isLove = binding.rgLoveMu.checkedRadioButtonId == R.id.rb_yes

        if (name.isEmpty()){
            binding.edtName.error = FIELD_REQUIRED
            return
        }

        if (email.isEmpty()){
            binding.edtEmail.error = FIELD_REQUIRED
            return
        }

        if (!isValidEmail(email)){
            binding.edtEmail.error = FIELD_IS_NOT_VALID
            return
        }

        if (age.isEmpty()){
            binding.edtAge.error = FIELD_REQUIRED
            return
        }
        if (phoneNo.isEmpty()){
            binding.edtPhone.error = FIELD_REQUIRED
            return
        }

        if (!TextUtils.isDigitsOnly(phoneNo)){
            binding.edtPhone.error = FIELD_DIGIT_ONLY
            return
        }

        saveUser(name,email,age,phoneNo,isLove)
        /*
        Pada FormUserPreference kita memberikan result (hasil) dengan cara berikut:
         */
        val resultIntent = Intent()
        resultIntent.putExtra(EXTRA_RESULT,userModel)
        setResult(RESULT_CODE,resultIntent)

        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}