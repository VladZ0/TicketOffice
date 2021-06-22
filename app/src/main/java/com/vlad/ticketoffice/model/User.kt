package com.vlad.ticketoffice.model

import java.io.Serializable

class User(var uid: String = "", var name: String = "", var email: String = "",
           var role: Role = Role.USER, var status: Status = Status.OFFLINE,
           var bookedTickets: MutableList<HashMap<String, String>> = mutableListOf(),
           var cards: MutableList<Card> = mutableListOf()) : Serializable{

    companion object{
        const val TABLE_NAME = "users"
        const val TICKET_RACE_ID = "ticket_race_id"
        const val TICKET_SEAT_NUM = "ticket_seat_num"
        const val BOOKED_TICKETS = "bookedTickets"
        const val STATUS = "status"
        const val CARDS = "cards"
    }

    enum class Status{
        ONLINE, OFFLINE
    }

    enum class Role{
        ADMIN, USER
    }
}