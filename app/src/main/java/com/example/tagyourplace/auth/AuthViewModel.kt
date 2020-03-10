package com.example.tagyourplace.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.AuthCredential

class AuthViewModel (val authRepository: AuthRepository) : ViewModel() {
    var authUser : LiveData<User> = MutableLiveData()

    fun signInWithGoogle(authCredential: AuthCredential){
        authUser = authRepository.firebaseSignInWithGoogle(authCredential)
    }
}