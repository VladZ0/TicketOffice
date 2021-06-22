package com.vlad.ticketoffice.UserDataActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.vlad.ticketoffice.model.Card
import com.vlad.ticketoffice.model.User

class UserDataActivityViewModel: ViewModel() {
    var cards: MutableLiveData<List<Card>> = MutableLiveData()
    private lateinit var mUserListenerReg: ListenerRegistration

    private val db = Firebase.firestore
    private val mAuth = FirebaseAuth.getInstance()

    fun initSnapshotListener(user: User){
        mUserListenerReg = db.collection(User.TABLE_NAME).document(user.uid)
            .addSnapshotListener { value, _ ->
                if(value != null){
                    cards.value = value.toObject<User>()?.cards
                }
            }
    }

    fun removeSnapshotListeners(){
        mUserListenerReg.remove()
    }

    fun setOnlineStatus(){
        mAuth.uid?.let { db.collection(User.TABLE_NAME).document(it).update(User.STATUS, User.Status.ONLINE) }
    }

    fun setOfflineStatus(){
        mAuth.uid?.let { db.collection(User.TABLE_NAME).document(it).update(User.STATUS, User.Status.OFFLINE) }
    }
}