package com.vlad.ticketoffice.MainActivity.ui.MainFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.vlad.ticketoffice.model.Race
import com.vlad.ticketoffice.model.User

class MainFragmentViewModel: ViewModel() {
    var races: MutableLiveData<MutableList<Race>> = MutableLiveData()
    private lateinit var mCurrentRacesListenerReg: ListenerRegistration
    private val db = Firebase.firestore

    fun initSnapshotListeners() {

        mCurrentRacesListenerReg = db.collection(Race.TABLE_NAME)
            .addSnapshotListener { documents, _ ->
                races.value = documents?.toObjects<Race>() as MutableList<Race>
            }

    }

    fun removeSnapshotListeners(){
        mCurrentRacesListenerReg.remove()
    }

    fun deleteRace(race: Race){
        db.collection(User.TABLE_NAME).get()
            .addOnSuccessListener {
                val users = it.toObjects<User>()

                db.collection(Race.TABLE_NAME).document(race.id).delete()
                    .addOnSuccessListener {
                        for(ticket in race.ticketsList!!){
                            if(ticket.isBooked) {
                                for(user in users){
                                    if(user.uid == ticket.ownerId){
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
                    .addOnFailureListener {
                        it.printStackTrace()
                    }
            }

    }

}