package com.vlad.ticketoffice.MainActivity.ui.PrivateOfficeFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.vlad.ticketoffice.model.Race
import com.vlad.ticketoffice.model.Ticket
import com.vlad.ticketoffice.model.User

class FragmentPrivateOfficeViewModel: ViewModel() {
    private val db = Firebase.firestore
    private val mAuth = FirebaseAuth.getInstance()
    private lateinit var mTicketInfoListenerReg: ListenerRegistration
    val bookedRaces: MutableLiveData<List<Race>> = MutableLiveData()

    fun initSnapshotListeners(){
        mAuth.uid?.let { uid ->
            mTicketInfoListenerReg = db.collection(User.TABLE_NAME).document(uid)
                .addSnapshotListener{ value, _ ->
                    if(value != null){
                        val currentUser = value.toObject<User>()
                        db.collection(Race.TABLE_NAME).get()
                            .addOnSuccessListener { snapshot ->
                                val races = mutableListOf<Race>()

                                for (ticket in currentUser?.bookedTickets!!){
                                    races.add(snapshot.toObjects<Race>().filter { race ->
                                        race.id == ticket[User.TICKET_RACE_ID]
                                    }[0].apply {
                                        this.ticketsList = this.ticketsList?.filter {
                                            it.ownerId == uid
                                                    && it.seatNum == ticket[User.TICKET_SEAT_NUM]?.toInt()
                                        } as MutableList<Ticket>
                                    })
                                }

                                bookedRaces.value = races
                            }
                    }
                }


        }
    }

    fun removeSnapshotListeners(){
        mTicketInfoListenerReg.remove()
    }
}