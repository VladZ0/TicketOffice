package com.vlad.ticketoffice.model

import com.vlad.ticketoffice.Utils
import java.io.Serializable

class Race(var id: String = "", var name: String = "", var route: String = "",
           var departureDate: String = "", var departureTime: String = "", var arrivalTime: String = "",
           var ordinaryTicketPrice: Int = 0, var expensiveTicketPrice: Int = 0,
           var ticketsList: MutableList<Ticket>? = null): Serializable {

    init {
        if(this.ticketsList == null) {
            initTickets()
        }
    }


    companion object{
        const val TABLE_NAME = "races"
        const val TICKETS_LIST = "ticketsList"
    }

    private fun initTickets(){
        this.ticketsList = mutableListOf()

        for (i in 0 until Utils.SEATS_COUNT){
            val ticket = Ticket(seatNum = i + 1)

            if(i < 24) {
                ticket.price = this.expensiveTicketPrice
            }
            else{
                ticket.price = this.ordinaryTicketPrice
            }

            ticket.raceId = this.id

            this.ticketsList!!.add(ticket)
        }
    }
}