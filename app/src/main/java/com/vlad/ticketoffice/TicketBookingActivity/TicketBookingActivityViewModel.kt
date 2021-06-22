package com.vlad.ticketoffice.TicketBookingActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.vlad.ticketoffice.model.Card
import com.vlad.ticketoffice.model.Race
import com.vlad.ticketoffice.model.Ticket
import com.vlad.ticketoffice.model.User

class TicketBookingActivityViewModel: ViewModel() {
    var currentTicket: MutableLiveData<Ticket> = MutableLiveData()
    var customer: MutableLiveData<User> = MutableLiveData()
    var currentUser: MutableLiveData<User> = MutableLiveData()
    private lateinit var mCurrentRaceListenerReg: ListenerRegistration
    private lateinit var mCurrentUserListenerReg: ListenerRegistration
    private lateinit var mCurrentCustomerListenerReg: ListenerRegistration
    private val db = Firebase.firestore
    private val mAuth = FirebaseAuth.getInstance()

    fun initSnapshotListeners(currentTicket: Ticket){
        initSnapshotCurrentUserListener()
        initCustomerSnapshotListener(currentTicket)
        initRaceSnapshotListener(currentTicket)
    }

    private fun initRaceSnapshotListener(ticket: Ticket){
        mCurrentRaceListenerReg = db.collection(Race.TABLE_NAME).document(ticket.raceId)
            .addSnapshotListener { value, _ ->
                val changedRace = value?.toObject<Race>()

                if(ticket.seatNum <= 24){
                    changedRace?.ticketsList?.get(ticket.seatNum - 1)?.price = changedRace?.expensiveTicketPrice!!
                }
                else{
                    changedRace?.ticketsList?.get(ticket.seatNum - 1)?.price = changedRace?.ordinaryTicketPrice!!
                }

                db.collection(Race.TABLE_NAME).document(ticket.raceId)
                    .update(Race.TICKETS_LIST, changedRace.ticketsList)
                    .addOnSuccessListener {
                        db.collection(Race.TABLE_NAME).document(ticket.raceId).get()
                            .addOnSuccessListener {
                                if(ticket.seatNum <= 24){
                                    currentTicket.value = value.toObject<Race>()?.ticketsList?.get(ticket.seatNum - 1)
                                }
                                else{
                                    currentTicket.value = value.toObject<Race>()?.ticketsList?.get(ticket.seatNum - 1)
                                }
                            }
                    }
            }

    }

    private fun initSnapshotCurrentUserListener(){
        mAuth.uid?.let {
            mCurrentUserListenerReg = db.collection(User.TABLE_NAME).document(it)
                .addSnapshotListener { value, _ ->
                    currentUser.value = value?.toObject<User>()
                }
        }
    }

    private fun initCustomerSnapshotListener(ticket: Ticket){
        mCurrentCustomerListenerReg = db.collection(User.TABLE_NAME).document(ticket.ownerId)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    customer.value = value.toObject<User>()
                }
            }
    }

    fun removeSnapshotListeners(){
        mCurrentRaceListenerReg.remove()
        mCurrentCustomerListenerReg.remove()
        mCurrentUserListenerReg.remove()
    }

    fun bookTicket(ticket: Ticket){
        mAuth.uid?.let { currentUserId ->
            db.collection(User.TABLE_NAME).document(currentUserId).get()
                .addOnSuccessListener { doc ->
                    val updatedBookedTicketsList = doc.toObject<User>()?.bookedTickets
                    updatedBookedTicketsList?.add(hashMapOf(
                        User.TICKET_RACE_ID to ticket.raceId,
                        User.TICKET_SEAT_NUM to ticket.seatNum.toString()
                    ))

                    db.collection(User.TABLE_NAME).document(currentUserId)
                        .update(User.BOOKED_TICKETS, updatedBookedTicketsList)
                        .addOnSuccessListener {
                            db.collection(Race.TABLE_NAME).document(ticket.raceId).get()
                                .addOnSuccessListener { raceDoc ->
                                    val updatedTicketsList = raceDoc.toObject<Race>()?.ticketsList
                                    updatedTicketsList?.get(ticket.seatNum - 1)?.isBooked = true
                                    updatedTicketsList?.get(ticket.seatNum - 1)?.ownerId = currentUserId

                                    db.collection(Race.TABLE_NAME).document(ticket.raceId)
                                        .update(Race.TICKETS_LIST, updatedTicketsList)
                                }
                        }
                }
        }
    }

    fun saveCardData(card: Card){
        mAuth.uid?.let {
            db.collection(User.TABLE_NAME).document(it).get()
                .addOnSuccessListener { snapshot ->
                    val cards: MutableList<Card>? = snapshot.toObject<User>()?.cards
                    if(cards != null && !cards.contains(card)){
                        cards.add(card)
                        db.collection(User.TABLE_NAME).document(it).update(User.CARDS, cards)
                    }
                }
        }
    }

    fun setOnlineStatus(){
        mAuth.uid?.let { db.collection(User.TABLE_NAME).document(it).update(User.STATUS, User.Status.ONLINE) }
    }

    fun setOfflineStatus(){
        mAuth.uid?.let { db.collection(User.TABLE_NAME).document(it).update(User.STATUS, User.Status.OFFLINE) }
    }
}