package com.vlad.ticketoffice.MainActivity

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.vlad.ticketoffice.model.User

class MainActivityViewModel: ViewModel() {
    var user: MutableLiveData<User> = MutableLiveData()
    private val db = Firebase.firestore
    private val mAuth = FirebaseAuth.getInstance()
    private var mCurrentListenerRegistration: ListenerRegistration? = null

    fun initUserSnapshotListener(){
        mAuth.currentUser?.let {
            mCurrentListenerRegistration = db.collection(User.TABLE_NAME).document(it.uid)
                .addSnapshotListener { value, error ->
                    if(value != null){
                        if(value.toObject<User>()?.status == User.Status.ONLINE) {
                            user.value = value.toObject<User>()
                        }
                    }
                    else{
                        error?.printStackTrace()
                    }
            }
        }
    }

    fun removeSnapshotListeners(){
        mCurrentListenerRegistration?.remove()
    }

    fun setOnlineStatus(){
        mAuth.uid?.let { db.collection(User.TABLE_NAME).document(it).update(User.STATUS, User.Status.ONLINE) }
    }

    fun setOfflineStatus(){
        mAuth.uid?.let { db.collection(User.TABLE_NAME).document(it).update(User.STATUS, User.Status.OFFLINE) }
    }
}