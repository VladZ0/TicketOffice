package com.vlad.ticketoffice.MainActivity.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vlad.ticketoffice.R
import com.vlad.ticketoffice.databinding.RaceItemBinding
import com.vlad.ticketoffice.model.Race

class RacesAdapter :
    RecyclerView.Adapter<RacesAdapter.RaceViewHolder> {

    var races: MutableList<Race>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var raceItemLongClickListener: OnRaceItemLongClickListener? = null
    var raceItemClickListener: OnRaceItemClickListener? = null

    constructor(races: MutableList<Race>? = null,
                raceItemLongClickListener: OnRaceItemLongClickListener? = null,
                raceItemClickListener: OnRaceItemClickListener? = null){
        this.races = races
        this.raceItemLongClickListener = raceItemLongClickListener
        this.raceItemClickListener = raceItemClickListener
    }

    interface OnRaceItemLongClickListener{
        fun onRaceItemLongClick(v: View?, position: Int)
    }

    interface  OnRaceItemClickListener{
        fun onRaceItemClick(v: View?, position: Int)
    }

    class RaceViewHolder(itemView: View,
                         private var itemLongClickListener: OnRaceItemLongClickListener?,
                         private var itemClickListener: OnRaceItemClickListener?)
        : RecyclerView.ViewHolder(itemView), View.OnLongClickListener, View.OnClickListener{
        val mBinding = RaceItemBinding.bind(itemView)

        init {
            itemView.setOnLongClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            itemLongClickListener?.onRaceItemLongClick(v, adapterPosition)

            return true
        }

        override fun onClick(view: View?) {
            itemClickListener?.onRaceItemClick(view, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        return RaceViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.race_item, parent, false),
            raceItemLongClickListener, raceItemClickListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RaceViewHolder, position: Int) {
        holder.mBinding.tvRaceRoute.text = races?.get(position)?.route
        holder.mBinding.tvRace.text = races?.get(position)?.name
        holder.mBinding.tvRaceDepartureDate.text = races?.get(position)?.departureDate
        holder.mBinding.tvRaceDepartureTime.text = races?.get(position)?.departureTime
        holder.mBinding.tvRaceArrivalTime.text = races?.get(position)?.arrivalTime
        holder.mBinding.tvRaceOrdinaryTicketPrice.text = "${races?.get(position)?.ordinaryTicketPrice} Грн"
        holder.mBinding.tvRaceExpensiveTicketPrice.text = "${races?.get(position)?.expensiveTicketPrice} Грн"
    }

    override fun getItemCount(): Int {
        races?.let {
            return it.size
        }

        return 0
    }
}