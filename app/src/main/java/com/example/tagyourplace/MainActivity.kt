package com.example.tagyourplace

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.example.tagyourplace.adapters.ViewPagerAdapter
import com.example.tagyourplace.camera.CameraFragment
import com.example.tagyourplace.camera.CameraFragmentPost
import com.example.tagyourplace.camera.ShowFirstPageListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() , ShowFirstPageListener {


    companion object {
        private const val TAG = "MainActivity"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG,"success")

        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        viewPager.addOnPageChangeListener(onPageListener)
        val db = FirebaseFirestore.getInstance()

    }
    private fun startFirstFragment(){
      supportFragmentManager
          ?.beginTransaction()
          ?.replace(R.id.rootFrame,CameraFragment(),"Fragment1")
          ?.commit()

    }
    private fun checkFirstFragment(){
       val fragment =  supportFragmentManager
            ?.findFragmentByTag("Fragment1")
        if ( fragment != null ){
            Log.d(TAG,"Fragment1 is not null")
            removeFirstFragment(fragment)
        }



    }
    private fun checkSecondFragment(): Boolean {
        val fragment2 = supportFragmentManager?.findFragmentByTag("Fragment2")
        if (fragment2 != null){
            Log.d(TAG,"Fragment2 is not null")
            return false
        }
        return true
    }
    private fun removeFirstFragment(fragment: Fragment) {
        supportFragmentManager
            ?.beginTransaction()
            ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            ?.remove(fragment)
            ?.commit()
        Log.d(TAG,"Fragment1 deleted")
    }
    private val onPageListener = object : ViewPager.OnPageChangeListener{
        override fun onPageScrollStateChanged(state: Int) {
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {
            if (position == 0){
                checkFirstFragment()
            }
           if (position == 1){
               if (checkSecondFragment())
                startFirstFragment()
           }
        }

    }

    override fun showFirstPage() {
        val fragment2 = supportFragmentManager?.findFragmentByTag("Fragment2")
        val fragment =  supportFragmentManager?.findFragmentByTag("Fragment1")
        Log.d(TAG,supportFragmentManager.backStackEntryCount.toString())
        supportFragmentManager.backStackEntryCount
        //viewPager.setCurrentItem(0)
        if (fragment2 != null && fragment != null){
           supportFragmentManager
                ?.beginTransaction()
               // ?.remove(fragment)
                ?.remove(fragment2)

                ?.commitNow()
            Log.d(TAG,"Fragment2 deleted")


        }
        supportFragmentManager.popBackStackImmediate()

        viewPager.setCurrentItem(0)

        //checkFirstFragment()

    }
}
