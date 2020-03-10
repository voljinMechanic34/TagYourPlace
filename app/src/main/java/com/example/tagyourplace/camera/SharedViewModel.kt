package com.example.tagyourplace.camera

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    val pathUri : MutableLiveData<Uri> = MutableLiveData()
    fun setPathUri(uri : Uri){
        pathUri.value = uri

    }

    fun getPathUri() : LiveData<Uri> {

        return pathUri
    }
}