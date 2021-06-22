package com.vlad.ticketoffice.MainActivity.ui.LoginFragment

import android.app.Application
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vlad.ticketoffice.MainActivity.MainActivity
import com.vlad.ticketoffice.model.User

class LoginFragmentViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Firebase.firestore
    private val mAuth = FirebaseAuth.getInstance()

    // Create user and forward him to MainActivity

    fun signUp(name: String, email: String, password: String){
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                mAuth.currentUser?.uid?.let{
                    val user = User(it, name, email, User.Role.USER, User.Status.ONLINE)

                    db.collection(User.TABLE_NAME).document(user.uid).set(user)
                        .addOnSuccessListener {
                            val intent = Intent(getApplication(), MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

                            getApplication<Application>().startActivity(intent)
                        }
                        .addOnFailureListener { ex ->
                            Toast.makeText(getApplication(), "Exception: " + ex.message, Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Неправильно введений пароль(ПАРОЛЬ МІНІМУМ 6 СИМВОЛІВ) або адреса!",
                    Toast.LENGTH_LONG)
                    .show()
            }
    }

    fun signIn(email: String, password: String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val intent = Intent(getApplication(), MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP

                getApplication<Application>().startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(getApplication(), "Неправильно введений пароль або адреса!", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}