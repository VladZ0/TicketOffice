package com.vlad.ticketoffice.MainActivity.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vlad.ticketoffice.R
import com.vlad.ticketoffice.databinding.TicketInfoItemBinding
import com.vlad.ticketoffice.model.Race
import com.vlad.ticketoffice.model.Ticket

class TicketsInfoAdapter:
    RecyclerView.Adapter<TicketsInfoAdapter.TicketInfoViewHolder> {

    var ticketsInfo: MutableList<Race>? = null
        set(value){
            field = value
            notifyDataSetChanged()
        }

    constructor(ticketsInfo: MutableList<Race>? = null){
        this.ticketsInfo = ticketsInfo
    }

    class TicketInfoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val mBinding = TicketInfoItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketInfoViewHolder {
        return TicketInfoViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.ticket_info_item, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TicketInfoViewHolder, position: Int) {
        holder.mBinding.tvRaceRoute.text = ticketsInfo?.get(position)?.route
        holder.mBinding.tvRace.text = ticketsInfo?.get(position)?.name
        holder.mBinding.tvRaceArrivalTime.text = ticketsInfo?.get(position)?.arrivalTime
        holder.mBinding.tvRaceDepartureTime.text = ticketsInfo?.get(position)?.departureTime
        holder.mBinding.tvRaceDepartureDate.text = ticketsInfo?.get(position)?.departureDate
        holder.mBinding.tvSeatNum.text = ticketsInfo?.get(position)?.ticketsList?.get(0)?.seatNum.toString()
        holder.mBinding.tvTicketPrice.text = ticketsInfo?.get(position)?.ticketsList?.get(0)?.price.toString() + " Грн"
        if(ticketsInfo?.get(position)?.ticketsList?.get(0)?.seatClass == Ticket.SeatClass.ORDINARY)
            holder.mBinding.tvSeatClass.text = "економ клас"
        else
            holder.mBinding.tvSeatClass.text = "бізнес клас"

    }

    override fun getItemCount(): Int {
        return if(ticketsInfo != null){
            ticketsInfo?.size!!
        } else{
            0
        }
    }

}