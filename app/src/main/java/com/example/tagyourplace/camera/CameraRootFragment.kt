package com.example.tagyourplace.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tagyourplace.R


class CameraRootFragment : Fragment() {

    private val TAG = "RootFragment"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {



        return inflater.inflate(R.layout.fragment_camera_root,container,false)


    }
}