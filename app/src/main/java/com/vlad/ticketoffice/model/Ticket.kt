package com.vlad.ticketoffice.model

import java.io.Serializable

class Ticket(var raceId: String = "", var ownerId: String = "none", var seatNum: Int = 0, var price: Int = 0,
             var seatClass: SeatClass = SeatClass.ORDINARY, var isBooked: Boolean = false): Serializable{

    enum class SeatClass{
        ORDINARY, EXPENSIVE
    }
}