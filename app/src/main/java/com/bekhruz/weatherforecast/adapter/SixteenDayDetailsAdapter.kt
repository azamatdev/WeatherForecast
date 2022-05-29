package com.bekhruz.weatherforecast.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bekhruz.weatherforecast.databinding.ItemSixteenDayDetailsBinding
import com.bekhruz.weatherforecast.network.sixteendayweather.Data
import com.bekhruz.weatherforecast.viewmodels.WeatherViewModel

class SixteenDayDetailsAdapter(private val viewModel: WeatherViewModel) :
    ListAdapter<Data, SixteenDayDetailsAdapter.SixteenDayDetailsViewHolder>(DiffCallBack) {

    class SixteenDayDetailsViewHolder(private val binding: ItemSixteenDayDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Data, viewModel: WeatherViewModel) {
            binding.apply {
                weekDaysTextview.text = data.ts?.toLong()?.let { viewModel.getTime(it, "weekday") }
                iconWeekdaysStatus.load(
                    data.weather?.icon?.let { viewModel.getIconsOfSixteenDayData(it).toUri().buildUpon().scheme("https").build() }
                ) {
                    //TODO: ADD PLACEHOLDER, ERROR HANDLING FOR COIL
                }
                rainStatusWeekdaysTextview.text =
                    String.format("%d%% rain", data.pop)
                lowTempWeekdaysTextview.text = data.min_temp.toString()
                highTempWeekdaysTextview.text = String.format(" / %s",data.max_temp.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SixteenDayDetailsViewHolder {
        return SixteenDayDetailsViewHolder(
            ItemSixteenDayDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }


    override fun onBindViewHolder(holder: SixteenDayDetailsViewHolder, position: Int) {
        val elementOfSevenDayDetails = getItem(position)
        holder.bind(elementOfSevenDayDetails, viewModel)
    }

    companion object DiffCallBack : DiffUtil.ItemCallback<Data>() {
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.temp == newItem.temp
        }
    }
}