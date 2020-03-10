package com.example.tagyourplace

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_auth.*
import org.koin.android.ext.android.inject
import com.google.android.gms.tasks.Task
import androidx.annotation.NonNull
import androidx.lifecycle.Observer
import com.example.tagyourplace.auth.AuthRepository
import com.example.tagyourplace.auth.AuthViewModel
import com.example.tagyourplace.auth.User
import com.google.android.material.snackbar.Snackbar
import org.koin.android.viewmodel.ext.android.viewModel


class AuthActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
    private val auth : FirebaseAuth by inject()

    private val googleSignInClient : GoogleSignInClient by inject()
    private val authViewModel  by viewModel<AuthViewModel>()
    private val authRepository : AuthRepository by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

       Log.d("Activity",authViewModel.toString())
        Log.d("Activity",authRepository.toString())
        button_auth.setOnClickListener{
            signIn()
        }
       /* button_out.setOnClickListener{
            logout()
        }*/


    }

    private fun logout() {

        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this,
            OnCompleteListener<Void> { Toast.makeText(this,"123",Toast.LENGTH_SHORT).show() })
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                account?.let {firebaseAuthWithGoogle(account)}

            } catch (e : ApiException){
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.id!!)
        val credential  = GoogleAuthProvider.getCredential(account.idToken,null)
        authViewModel.signInWithGoogle(credential)
        authViewModel.authUser.observe(this, Observer {user->
            if (user != null){
                startMainActivity(user)
                Log.w(TAG, "sign in")
            } else{
                showError()
            }
        })

    }

    private fun showError() {
        Toast.makeText(this,"oops",Toast.LENGTH_SHORT).show()

    }

    private fun startMainActivity(user : User){
        val intent = Intent(this,MainActivity::class.java)
        val gson = Gson()
        intent.putExtra("userExtra",gson.toJson(user))
        startActivity(intent)

    }
}
