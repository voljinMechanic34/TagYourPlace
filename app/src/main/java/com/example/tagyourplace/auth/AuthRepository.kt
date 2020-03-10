package com.example.tagyourplace.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject
import java.util.*


class AuthRepository (val firebaseAuth: FirebaseAuth){



    fun firebaseSignInWithGoogle(authCredential: AuthCredential): LiveData<User> {
        val authUser : MutableLiveData<User> = MutableLiveData()

        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    user?.let{
                        val uid = user.uid
                        val userName = user.displayName
                        val email = user.email
                        val photoUrl = Objects.requireNonNull(user.photoUrl.toString())
                        val user = User(uid,userName,email,photoUrl)
                        authUser.value = user
                    }
                }
            else {
                authUser.value = null
            }
        }
            return authUser
        }
    }
