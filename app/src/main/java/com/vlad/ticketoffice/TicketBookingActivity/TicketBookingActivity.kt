package com.vlad.ticketoffice.TicketBookingActivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.vlad.ticketoffice.Utils
import com.vlad.ticketoffice.databinding.ActivityTicketBookingBinding
import com.vlad.ticketoffice.model.Card
import com.vlad.ticketoffice.model.Ticket
import com.vlad.ticketoffice.model.User

class TicketBookingActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityTicketBookingBinding
    private lateinit var mViewModel: TicketBookingActivityViewModel
    private lateinit var currentTicket: Ticket

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTicketBookingBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent != null) {
            currentTicket = intent.getSerializableExtra(Utils.CURRENT_TICKET) as Ticket
        }

        initViewModel()

        mBinding.tvSeatNum.append(currentTicket.seatNum.toString())

        mBinding.bookTicketBtn.setOnClickListener {
            if(validateFields()) {
                mViewModel.bookTicket(currentTicket)
                currentTicket.isBooked = true
                mViewModel.saveCardData(
                    Card(
                        mBinding.etCardNumber.text.trim().toString(),
                        mBinding.etCvvNumber.text.toString(),
                        mBinding.etPinNumber.text.trim().toString()
                    )
                )
                Toast.makeText(
                    this,
                    "Ваші дані обробляються, зачекайте...",
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViewModel(){
        mViewModel = ViewModelProvider(this).get(TicketBookingActivityViewModel::class.java)

        mViewModel.currentUser.observe(this) {
            if(it.role == User.Role.USER && currentTicket.isBooked){
                mBinding.bookTicketBtn.visibility = View.GONE
                mBinding.cardForm.visibility = View.GONE
            }
            else if(it.role == User.Role.USER && !currentTicket.isBooked){
                mBinding.bookTicketBtn.visibility = View.VISIBLE
                mBinding.cardForm.visibility = View.VISIBLE
            }
            else if (it.role == User.Role.ADMIN && currentTicket.isBooked) {
                mBinding.bookTicketBtn.visibility = View.GONE
                mBinding.cardForm.visibility = View.GONE
                mBinding.tvTicketCustomerName.visibility = View.VISIBLE
                mBinding.tvTicketCustomerEmail.visibility = View.VISIBLE
            }
            else {
                mBinding.bookTicketBtn.visibility = View.GONE
                mBinding.cardForm.visibility = View.GONE
                mBinding.tvTicketCustomerName.visibility = View.GONE
                mBinding.tvTicketCustomerEmail.visibility = View.GONE
            }
        }

        mViewModel.currentTicket.observe(this) {
            currentTicket = it

            if (currentTicket.isBooked) {
                mBinding.tvBookingStatus.visibility = View.VISIBLE
            }
            else {
                mBinding.tvBookingStatus.visibility = View.GONE
            }

            mBinding.tvSeatPrice.text = "Ціна білета: ${currentTicket.price} грн"
            if (currentTicket.seatClass == Ticket.SeatClass.ORDINARY) {
                mBinding.tvSeatClass.text = "Тип місця: економ клас"
            }
            else {
                mBinding.tvSeatClass.text = "Тип місця: бізнес клас"
            }
        }

        mViewModel.customer.observe(this){
            mBinding.tvTicketCustomerName.text = "Покупець білета: ${mViewModel.customer.value?.name}"
            mBinding.tvTicketCustomerEmail.text = "Електронна адреса покупця: ${mViewModel.customer.value?.email}"
        }
    }

    override fun onStart() {
        super.onStart()
        mViewModel.setOnlineStatus()
        mViewModel.initSnapshotListeners(currentTicket)
    }

    override fun onPause() {
        super.onPause()
        mViewModel.removeSnapshotListeners()
        mViewModel.setOfflineStatus()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun validateFields(): Boolean{
        when {
            mBinding.etCardNumber.text.trim().toString().matches("[0-9]{16}".toRegex()) -> {
                Toast.makeText(this, "Введіть номер карти, формат: xxxxxxxxxxxxxxxx", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            mBinding.etCvvNumber.text.trim().toString().matches("[0-9]{3}".toRegex()) -> {
                Toast.makeText(this, "Введіть cvv, формат: xxx", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            mBinding.etPinNumber.text.trim().toString().matches("[0-9]{4}".toRegex()) -> {
                Toast.makeText(this, "Введіть пін-код, формат: xxxx", Toast.LENGTH_SHORT)
                    .show()
                return false
            }
            else -> return true
        }

    }
}