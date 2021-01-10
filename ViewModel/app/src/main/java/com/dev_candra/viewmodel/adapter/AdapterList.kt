package com.dev_candra.viewmodel.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dev_candra.viewmodel.R
import com.dev_candra.viewmodel.model.WeatherItems
import kotlinx.android.synthetic.main.list_item_layout.view.*

class AdapterList() : RecyclerView.Adapter<AdapterList.WeatherViewHolder>(){

    private val list = ArrayList<WeatherItems>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterList.WeatherViewHolder {
        // Your Code
        val inflateLayoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.list_item_layout,parent,false)
        return WeatherViewHolder(inflateLayoutInflater)
    }

    fun setData(items: ArrayList<WeatherItems>){
        list.clear()
        list.addAll(items)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        // Your Code
        return list.size
    }

    override fun onBindViewHolder(holder: AdapterList.WeatherViewHolder, position: Int) {
        // Your Code
        holder.bind(list[position])
    }

    inner class WeatherViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(weatherItems: WeatherItems){
            with(itemView){
                textCity.text = weatherItems.name
                textTemp.text = weatherItems.temperature
                textDesc.text = weatherItems.description
            }
        }
    }

}