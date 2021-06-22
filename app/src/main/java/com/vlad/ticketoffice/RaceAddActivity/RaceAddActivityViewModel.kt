package com.vlad.ticketoffice.RaceAddActivity

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.vlad.ticketoffice.model.Race
import com.vlad.ticketoffice.model.Ticket
import com.vlad.ticketoffice.model.User

class RaceAddActivityViewModel(application: Application): AndroidViewModel(application) {
    private val db = Firebase.firestore
    private val mAuth = FirebaseAuth.getInstance()
    var isEditing = false

    fun addRace(race: Race){
        db.collection(Race.TABLE_NAME).add(race)
            .addOnSuccessListener { docRef ->
                race.id = docRef.id

                db.collection(Race.TABLE_NAME).document(docRef.id).update("id", race.id)
                    .addOnFailureListener {
                        Toast.makeText(getApplication(), "Помилка при збереженні ідентифікатора", Toast.LENGTH_SHORT)
                            .show()
                    }

                db.collection(Race.TABLE_NAME).document(docRef.id).get()
                    .addOnSuccessListener {
                        val changedTicketsList = mutableListOf<Ticket>()
                        for(ticket in it.toObject<Race>()?.ticketsList!!){
                            ticket.raceId = race.id
                            if(ticket.seatNum <= 24){
                                ticket.seatClass = Ticket.SeatClass.EXPENSIVE
                            }
                            changedTicketsList.add(ticket)
                        }

                        db.collection(Race.TABLE_NAME).document(docRef.id)
                            .update(Race.TICKETS_LIST, changedTicketsList)
                    }

                Toast.makeText(getApplication(), "Дані збережено!", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Помилка при збережені даних!", Toast.LENGTH_SHORT)
                    .show()

                isEditing = false
            }
    }

    fun editRace(race: Race){
        db.collection(Race.TABLE_NAME).document(race.id).set(race)
            .addOnSuccessListener {
                Toast.makeText(getApplication(), "Дані збережено!", Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Помилка при збережені даних!", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    fun deleteTickets(race: Race){
        db.collection(User.TABLE_NAME).get()
            .addOnSuccessListener {
                val users = it.toObjects<User>()

                for(ticket in race.ticketsList!!){
                    Log.e("TAG", ticket.ownerId)
                    if(ticket.isBooked) {
                        for(user in users){
                            if(user.uid == ticket.ownerId){
                                Log.e("TAG", user.uid)
                                val newBookedList = user.bookedTickets
                                newBookedList.remove(hashMapOf(
                                    User.TICKET_RACE_ID to ticket.raceId,
                                    User.TICKET_SEAT_NUM to ticket.seatNum.toString()))

                                db.collection(User.TABLE_NAME).document(ticket.ownerId)
                                    .update(User.BOOKED_TICKETS, newBookedList)
                                    .addOnFailureListener { e ->
                                        e.printStackTrace()
                                    }
                                break
                            }
                        }
                    }
                }
            }

        val newRace = Race(race.id, race.name, race.route, race.departureDate, race.departureTime,
            race.arrivalTime, race.ordinaryTicketPrice, race.expensiveTicketPrice)

        db.collection(Race.TABLE_NAME).document(newRace.id).set(newRace)
            .addOnSuccessListener {
                val changedTicketsList = mutableListOf<Ticket>()
                for(ticket in newRace.ticketsList!!){
                    ticket.raceId = newRace.id
                    if(ticket.seatNum <= 24){
                        ticket.seatClass = Ticket.SeatClass.EXPENSIVE
                    }
                    else{
                        ticket.seatClass = Ticket.SeatClass.ORDINARY
                    }
                    changedTicketsList.add(ticket)
                }

                db.collection(Race.TABLE_NAME).document(newRace.id)
                    .update(Race.TICKETS_LIST, changedTicketsList)
            }
    }

    fun setOnlineStatus(){
        mAuth.uid?.let { db.collection(User.TABLE_NAME).document(it).update(User.STATUS, User.Status.ONLINE) }
    }

    fun setOfflineStatus(){
        mAuth.uid?.let { db.collection(User.TABLE_NAME).document(it).update(User.STATUS, User.Status.OFFLINE) }
    }
}