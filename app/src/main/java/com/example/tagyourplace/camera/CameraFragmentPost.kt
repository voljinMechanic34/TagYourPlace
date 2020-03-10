package com.example.tagyourplace.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.tagyourplace.R
import kotlinx.android.synthetic.main.fragment_camera_post.*
import java.io.File

class CameraFragmentPost : Fragment() {
    var viewModel: SharedViewModel? = null
    lateinit var path : Uri
    private var mListener : ShowFirstPageListener ? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera_post,container,false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ShowFirstPageListener){
            mListener = context
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        imageView_saveImage.setOnClickListener{
            galleryAddPic(path,requireContext())
            mListener?.showFirstPage()
        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

    }

    override fun onStart() {
        super.onStart()
        Log.d("TAG8","123")
        viewModel?.getPathUri()?.observe(viewLifecycleOwner, Observer {
            it.let {
                Log.d("TAG8",it.toString())

                Glide.with(requireContext()).load(it).into(imageView_Post)
                path = it
            }
        })
    }
    private fun galleryAddPic(path : Uri,context : Context){
        //val relativeLocation = Environment.DIRECTORY_PICTURES
        val file = File(path.path)
        //Glide.with(context).load(file).into(imageView3)
        val values = ContentValues()

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        //values.put(MediaStore.MediaColumns.OWNER_PACKAGE_NAME, "222132")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, file.path)
        //values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)

        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        // file.deleteOnExit()


    }

    override fun onDestroy() {
        super.onDestroy()
        mListener = null
    }
}

interface ShowFirstPageListener{
    fun showFirstPage()
}