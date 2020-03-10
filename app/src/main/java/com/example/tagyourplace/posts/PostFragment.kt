package com.example.tagyourplace.posts

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.tagyourplace.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_posts.*
import java.io.ByteArrayOutputStream

class PostFragment : Fragment() {
    var downloadUri : Uri? = null

    companion object{
        const val TAG = "PostFragment"
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = FirebaseFirestore.getInstance()

        addPost.setOnClickListener {
            setData(db)
        }
        getPosts.setOnClickListener{
            getData(db)
        }

    }
    fun setData(db : FirebaseFirestore){
        val post = hashMapOf(
            "location" to "Moscow"
        )
        db.collection("AllPosts")
            .add(post)
            .addOnSuccessListener { documentReference ->

                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")

            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }
        loadToStorage()
    }
    fun getData(db : FirebaseFirestore) {


        val mQuery  =  db.collection("AllPosts")
            .get()
            .addOnSuccessListener{
            for ( snapshot in it){
                Log.d(TAG, snapshot.id)
                val path = "s120033.jpg"

            }
                loadFromStorage("")
        }
            .addOnFailureListener{
                Log.d(TAG, it.toString())
            }
    }
    fun loadToStorage(){
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference

        val bitmap = BitmapFactory.decodeResource(resources,R.drawable.s12003)
        val nameFile = resources.getResourceEntryName(R.drawable.s12003)
        val nameLoadFile = "$nameFile.jpg"
        val imageRef = storageRef.child(nameLoadFile)
        Log.d("TAG",nameFile)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data = baos.toByteArray()

        var uploadTask = imageRef.putBytes(data)
        uploadTask
            .addOnSuccessListener {
                Log.d(TAG,"load image success")
            }
            .addOnFailureListener{
                Log.d(TAG,"load image failure")
            }

        getDownoloadUrl(uploadTask,imageRef)
    }
    fun getDownoloadUrl(task: UploadTask, imageRef: StorageReference) {
        task.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadUri = task.result
                Log.d(TAG, "URI IMAGE $downloadUri")

            } else {
                // Handle failures
                // ...
            }
        }
    }
    fun loadFromStorage(path : String){
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference.child("s120033.jpg")

        Glide.with(requireContext())
            .load(downloadUri)
            .into(imageViewPosts)

    }
}