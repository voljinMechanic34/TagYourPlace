package com.example.tagyourplace.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.*
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.tagyourplace.R
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.File

// This is an arbitrary number we are using to keep tab of the permission
// request. Where an app has multiple context for requesting permission,
// this can help differentiate the different contexts
private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest
private val REQUIRED_PERMISSIONS = arrayOf(
    Manifest.permission.CAMERA,
    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
class CameraFragment : Fragment() {
    lateinit var viewModel: SharedViewModel
    private lateinit var viewFinder: TextureView

    companion object{
        const val    TAG = "CameraFragment"

    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG,"create camera fragment")
        return inflater.inflate(R.layout.fragment_camera,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*button3.setOnClickListener{
            //startSecondFragment()
        }*/


        viewFinder = view.findViewById(R.id.view_finder)

    }

    override fun onStart() {
        super.onStart()
        // Request camera permissions
        if (allPermissionsGranted(requireContext())) {
            viewFinder.post { startCamera(requireContext()) }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // Every time the provided texture view changes, recompute layout
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }
    private fun startCamera(baseContext : Context) {
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetAspectRatio(Rational(1, 1))
            setTargetResolution(Size(640, 640))
        }.build()

        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // To update the SurfaceTexture, we have to remove it and re-add it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        // Add this before CameraX.bindToLifecycle

        // Create configuration object for the image capture use case
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                setTargetAspectRatio(Rational(1, 1))
                // We don't set a resolution for image capture; instead, we
                // select a capture mode which will infer the appropriate
                // resolution based on aspect ration and requested mode
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()

        // Build the image capture use case and attach button click listener
        val imageCapture = ImageCapture(imageCaptureConfig)
        lateinit var  file: File
        button_takePhoto.setOnClickListener {
            /*val file = File(externalMediaDirs.first(),
                "${System.currentTimeMillis()}.jpg")*/
            file = File.createTempFile("image","${System.currentTimeMillis()}.jpg"
                ,context?.externalMediaDirs?.first())
            imageCapture.takePicture(file,
                object : ImageCapture.OnImageSavedListener {
                    override fun onError(error: ImageCapture.UseCaseError,
                                         message: String, exc: Throwable?) {
                        val msg = "Photo capture failed: $message"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        Log.e(TAG, msg)
                        exc?.printStackTrace()
                    }

                    override fun onImageSaved(file: File) {
                        val msg = "Photo capture succeeded: ${file.absolutePath}"
                        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        //imageView.setImageBitmap(bitmap)
                        val uri = Uri.fromFile(file)
                        //Picasso.with(baseContext).load(uri).into(imageView)
                       // Glide.with(baseContext).load(uri).into(imageView)
                        viewModel.setPathUri(uri)
                        startSecondFragment()

                        Log.d(TAG, msg)
                    }
                })

        }
        /*button2.setOnClickListener{
           if (file != null){
               file.delete()
           }

        }*/
        CameraX.bindToLifecycle(viewLifecycleOwner, preview,imageCapture)
    }
    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when(viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Finally, apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted(requireContext())) {
                viewFinder.post { startCamera(requireContext()) }
            } else {
                Toast.makeText(requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                //finish()
                activity?.finish()
            }
        }
    }
    private fun allPermissionsGranted(baseContext : Context) = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
    private fun startSecondFragment(){
        fragmentManager?.beginTransaction()?.
            replace(R.id.rootFrame,CameraFragmentPost(),"Fragment2")?.
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)?.
            addToBackStack(null)?.
            commit()
    }
    private fun finishFragment(){

        fragmentManager?.beginTransaction()?.
            remove(CameraFragment())
            ?.commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG,"destroy camera fragment")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG,"destroy view camera fragment")
    }
}