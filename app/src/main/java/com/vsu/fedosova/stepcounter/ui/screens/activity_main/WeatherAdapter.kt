package com.vsu.fedosova.stepcounter.ui.screens.activity_main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.vsu.fedosova.stepcounter.R
import com.vsu.fedosova.stepcounter.databinding.ListItemBinding

class WeatherAdapter(private val listener: Listener?) : ListAdapter<WeatherModel, WeatherAdapter.Holder>(
    Comparator()
) {

    class Holder(view: View, private val listener: Listener?) : RecyclerView.ViewHolder(view){
        private val binding = ListItemBinding.bind(view)
        var itemTemp : WeatherModel? = null
        init {
            itemView.setOnClickListener {
                itemTemp?.let { it1 -> listener?.onClick(it1) }
            }
        }

        fun bind(item: WeatherModel) = with(binding){
            itemTemp = item
            if (item.time.length > 10)
                tvDate.text = item.time.substring(11,16)
            else
                tvDate.text = item.time
            tvCondition.text = String(item.condition.toByteArray(Charsets.ISO_8859_1))
            tvTemp.text = item.currentTemp.ifEmpty {"${item.maxTemp}°C ~ ${item.minTemp}°C"}
            Picasso.get().load("https:" + item.imageUrl).into(im)
        }
    }

    class Comparator : DiffUtil.ItemCallback<WeatherModel>(){
        override fun areItemsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: WeatherModel, newItem: WeatherModel): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return Holder(view, listener)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    interface Listener{
        fun onClick(item: WeatherModel)
    }
}