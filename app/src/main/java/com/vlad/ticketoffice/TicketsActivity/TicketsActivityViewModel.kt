package com.vlad.ticketoffice.TicketsActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.vlad.ticketoffice.model.Race
import com.vlad.ticketoffice.model.User

class TicketsActivityViewModel: ViewModel() {
    val currentRace: MutableLiveData<Race> = MutableLiveData()
    var currentUserTickets: List<HashMap<String, String>> = listOf()
    private lateinit var mCurrentRaceListenerReg: ListenerRegistration
    private lateinit var mCurrentUserListenerReg: ListenerRegistration
    private val db = Firebase.firestore
    private val mAuth = FirebaseAuth.getInstance()

    fun initSnapshotListeners(currentRace: Race){
        initUserSnapshotListener()
        initRaceSnapshotListener(currentRace)
    }

    private fun initRaceSnapshotListener(race: Race){
        mCurrentRaceListenerReg = db.collection(Race.TABLE_NAME).document(race.id)
            .addSnapshotListener{ value, _ ->
                if(value != null) {
                    val changedRace = value.toObject<Race>()

                    currentRace.value = changedRace
                }
            }
    }

    private fun initUserSnapshotListener(){
        mAuth.uid?.let {
            mCurrentUserListenerReg = db.collection(User.TABLE_NAME).document(it)
                .addSnapshotListener { value, _ ->
                    currentUserTickets = value?.toObject<User>()?.bookedTickets!!
                }
        }
    }

    fun removeSnapshotListeners(){
        mCurrentRaceListenerReg.remove()
        mCurrentUserListenerReg.remove()
    }

    fun setOnlineStatus(){
        mAuth.uid?.let { db.collection(User.TABLE_NAME).document(it).update(User.STATUS, User.Status.ONLINE) }
    }

    fun setOfflineStatus(){
        mAuth.uid?.let { db.collection(User.TABLE_NAME).document(it).update(User.STATUS, User.Status.OFFLINE) }
    }
}