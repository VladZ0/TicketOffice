package com.vlad.ticketoffice.MainActivity.ui.LoginFragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class LoginFragmentVMFactory(private val application: Application):
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginFragmentViewModel(application) as T
    }
}