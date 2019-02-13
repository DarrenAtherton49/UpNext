package com.atherton.upnext.util.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*

class FragmentViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val fragmentTitleList: MutableList<String> = ArrayList()
    private val fragmentList: MutableList<Fragment> = ArrayList()

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getCount(): Int = fragmentList.count()

    override fun getPageTitle(position: Int): CharSequence = fragmentTitleList[position]

    fun addFragment(fragmentTitle: String, fragment: Fragment) {
        fragmentTitleList.add(fragmentTitle)
        fragmentList.add(fragment)
    }

    fun clear() {
        fragmentTitleList.clear()
        fragmentList.clear()
    }
}
