package com.vlad.ticketoffice.TicketsActivity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import com.vlad.ticketoffice.R
import com.vlad.ticketoffice.TicketBookingActivity.TicketBookingActivity
import com.vlad.ticketoffice.Utils
import com.vlad.ticketoffice.databinding.ActivityTicketsBinding
import com.vlad.ticketoffice.model.Race
import com.vlad.ticketoffice.model.User

class TicketsActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityTicketsBinding
    private lateinit var mViewModel: TicketsActivityViewModel

    private lateinit var currentRace: Race


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityTicketsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(mBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(intent != null){
            currentRace = intent.getSerializableExtra(Utils.CURRENT_RACE) as Race
        }

        mViewModel = ViewModelProvider(this).get(TicketsActivityViewModel::class.java)
        initGrid()
    }

    override fun onStart() {
        super.onStart()
        mViewModel.setOnlineStatus()
        mViewModel.initSnapshotListeners(currentRace)
    }

    override fun onPause() {
        super.onPause()
        mViewModel.removeSnapshotListeners()
        mViewModel.setOfflineStatus()
    }

    /* init grid with colors
    * green - ticket bought by current user
    * red - ticket bought other user
    * yellow - free business class seat ticket
    * blue - free comfort class seat ticket
    */

    private fun initGrid() {
        mViewModel.currentRace.observe(this) {
            currentRace = it

            for (i in 0 until Utils.SEATS_COUNT) {
                if(currentRace.ticketsList?.get(i)?.isBooked == true && mViewModel.currentUserTickets.contains(hashMapOf(
                        User.TICKET_RACE_ID to currentRace.ticketsList?.get(i)?.raceId!!,
                        User.TICKET_SEAT_NUM to currentRace.ticketsList?.get(i)?.seatNum.toString()
                    ))) {
                    (mBinding.ticketsGrid[i] as Button).setBackgroundColor(ContextCompat.getColor(this,
                        R.color.green
                    ))
                }
                else if (currentRace.ticketsList?.get(i)?.isBooked == true) {
                    (mBinding.ticketsGrid[i] as Button).setBackgroundColor(ContextCompat.getColor(this,
                        R.color.red
                    ))
                }
            }
        }

        for (i in 0 until Utils.SEATS_COUNT) {
            (mBinding.ticketsGrid[i] as Button).text = (i + 1).toString()
            (mBinding.ticketsGrid[i] as Button).setOnClickListener {
                val newIntent = Intent(this, TicketBookingActivity::class.java)
                newIntent.putExtra(Utils.CURRENT_TICKET, currentRace.ticketsList?.get(i))
                startActivity(newIntent)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            this.finish()
        }

        return super.onOptionsItemSelected(item)
    }
}