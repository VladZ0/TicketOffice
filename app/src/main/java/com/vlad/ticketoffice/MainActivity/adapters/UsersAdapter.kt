package com.vlad.ticketoffice.MainActivity.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vlad.ticketoffice.R
import com.vlad.ticketoffice.databinding.UserItemBinding
import com.vlad.ticketoffice.model.User

class UsersAdapter(var users: MutableList<User>? = null,
                   var userItemClickListener: OnUserItemClickListener? = null)
    : RecyclerView.Adapter<UsersAdapter.UserViewHolder>() {

    interface OnUserItemClickListener{
        fun onUserItemClick(view: View, position: Int)
    }

    class UserViewHolder(itemView: View, var userItemClickListener: OnUserItemClickListener? = null) :
        RecyclerView.ViewHolder(itemView) {
        val mBinding = UserItemBinding.bind(itemView)

        init {
            itemView.setOnClickListener {
                userItemClickListener?.onUserItemClick(it, adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.user_item, parent, false), this.userItemClickListener)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.mBinding.tvUsername.text = users?.get(position)?.name
        holder.mBinding.tvUserEmail.text = users?.get(position)?.email

        if(users?.get(position)?.role == User.Role.ADMIN){
            holder.mBinding.tvUserIsAdmin.visibility = View.VISIBLE
        }

        if(users?.get(position)?.status == User.Status.ONLINE) {
            holder.mBinding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green))
            holder.mBinding.tvStatus.text = "online"
        }
        else{
            holder.mBinding.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
            holder.mBinding.tvStatus.text = "offline"
        }
    }

    override fun getItemCount(): Int {
        users?.let {
            return it.size
        }
        return 0
    }

}