package com.example.tagyourplace.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.tagyourplace.camera.CameraFragment
import com.example.tagyourplace.camera.CameraRootFragment
import com.example.tagyourplace.posts.PostFragment

class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager,
    BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        var fragment : Fragment? = null
         when (position) {
            0 -> fragment = PostFragment()
            1 -> fragment = CameraRootFragment()
         }
        return fragment!!
    }

    override fun getCount(): Int {
        return 2
    }
}