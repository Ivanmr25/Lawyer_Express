package com.example.lawyerexpress.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter




// Adapter que se encarga de manejar los fragmentos que se mostrarán dentro del ViewPager de la TabbedActivity

class ViewPageAdapter(activity: FragmentActivity?) : FragmentStateAdapter(activity!!) {

    private val mFragmentList: ArrayList<Fragment> = ArrayList()

    fun addFragment(fragment: Fragment) {
        mFragmentList.add(fragment)

    }

    override fun getItemCount(): Int {
        return mFragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return mFragmentList[position]
    }
}