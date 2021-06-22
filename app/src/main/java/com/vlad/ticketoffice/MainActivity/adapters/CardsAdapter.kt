package com.vlad.ticketoffice.MainActivity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vlad.ticketoffice.R
import com.vlad.ticketoffice.databinding.UserCardItemBinding
import com.vlad.ticketoffice.model.Card

class CardsAdapter: RecyclerView.Adapter<CardsAdapter.CardViewHolder> {
    var cards: List<Card>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    constructor(cards: List<Card>? = null){
        this.cards = cards
    }

    class CardViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val mBinding = UserCardItemBinding.bind(itemView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        return CardViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.user_card_item, parent, false))
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.mBinding.tvCardNumber.text = cards?.get(position)?.number
        holder.mBinding.tvCvvNumber.text = cards?.get(position)?.cvv
        holder.mBinding.tvPinNumber.text = cards?.get(position)?.pin
    }

    override fun getItemCount(): Int {
        cards?.let{
            return it.size
        }

        return 0
    }
}