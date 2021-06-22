package com.vlad.ticketoffice.MainActivity.ui.UsersFragment

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.ktx.Firebase
import com.vlad.ticketoffice.model.Card
import com.vlad.ticketoffice.model.User

class UsersFragmentViewModel: ViewModel() {
    private val db = Firebase.firestore
    private val mAuth = FirebaseAuth.getInstance()
    private var mCurrentListenerRegistration: ListenerRegistration? = null

    var users: MutableLiveData<MutableList<User>> = MutableLiveData()
    var cards: MutableLiveData<MutableList<Card>> = MutableLiveData()
    var currentUserRole: MutableLiveData<User.Role> = MutableLiveData()

    fun initUsersSnapshotListener(){
        mAuth.uid?.let {
            mCurrentListenerRegistration = db.collection(User.TABLE_NAME)
                .addSnapshotListener { value, _ ->
                    if(value != null){
                        val list: MutableList<User> = value.toObjects<User>() as MutableList<User>
                        var currentUser: User? = null
                        for (user in list){
                            if (user.uid == it) {
                                currentUser  = user
                                currentUserRole.value = user.role
                            }
                        }
                        list.remove(currentUser)
                        users.value = list
                    }
                }
        }
    }


    fun removeSnapshotListeners(){
        mCurrentListenerRegistration?.remove()
    }

}