package com.example.tagyourplace.di


import android.app.Application
import android.content.Context
import com.example.tagyourplace.R
import com.example.tagyourplace.auth.AuthRepository
import com.example.tagyourplace.auth.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {
        factory<GoogleSignInOptions> {
            provideGoogleSignInOptions(androidApplication())
        }
        factory<GoogleSignInClient>{
                provideGoogleSignInClient(get(),get())
        }
        single<FirebaseAuth>{
            FirebaseAuth.getInstance()
        }
        single<AuthRepository>{
            AuthRepository(get())
        }
        viewModel { AuthViewModel(get()) }
    }
fun provideGoogleSignInOptions(application: Application): GoogleSignInOptions {
    return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(application.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
}
fun provideGoogleSignInClient(application: Application, googleSignInOptions : GoogleSignInOptions): GoogleSignInClient {
    return GoogleSignIn.getClient(application,googleSignInOptions)
}

