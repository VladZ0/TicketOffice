package com.vlad.ticketoffice.model

import java.io.Serializable

class Card(var number: String = "xxxxxxxxxxxxxxxx", var cvv: String = "xxx", var pin: String = "xxxx"):
    Serializable{
    companion object{
        const val TABLE_NAME = "cards"
    }
}