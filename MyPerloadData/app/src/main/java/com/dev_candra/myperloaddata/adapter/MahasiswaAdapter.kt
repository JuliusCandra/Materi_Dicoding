package com.dev_candra.myperloaddata.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev_candra.myperloaddata.R
import com.dev_candra.myperloaddata.databinding.ItemMahasiswaRawBinding
import com.dev_candra.myperloaddata.model.MahasiswaModel
import kotlin.random.Random

class MahasiswaAdapter : RecyclerView.Adapter<MahasiswaAdapter.MahasiswaHolder>(){

    private val listMahasiswa = ArrayList<MahasiswaModel>()

    fun setData(listMahasiswa: ArrayList<MahasiswaModel>) {

        if (listMahasiswa.size > 0) {
            this.listMahasiswa.clear()
        }

        this.listMahasiswa.addAll(listMahasiswa)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MahasiswaAdapter.MahasiswaHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mahasiswa_raw, parent, false)
        return MahasiswaHolder(view)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: MahasiswaAdapter.MahasiswaHolder, position: Int) {
        holder.bind(listMahasiswa[position])
    }

    inner class MahasiswaHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemMahasiswaRawBinding.bind(itemView)
        fun bind(mahasiswa: MahasiswaModel) {
            binding.txtNim.text = mahasiswa.nim
            binding.txtName.text = mahasiswa.name

            val random = java.util.Random()
            val color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
            binding.imageView.setColorFilter(color)
        }
    }

}