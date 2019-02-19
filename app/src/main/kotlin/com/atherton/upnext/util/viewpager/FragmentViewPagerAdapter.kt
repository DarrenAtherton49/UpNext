package com.atherton.upnext.util.viewpager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*

class FragmentViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val fragmentIdList: MutableList<Long> = ArrayList()
    private val fragmentTitleList: MutableList<String> = ArrayList()
    private val fragmentList: MutableList<Fragment> = ArrayList()

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getCount(): Int = fragmentList.count()

    override fun getPageTitle(position: Int): CharSequence = fragmentTitleList[position]

    fun addFragment(id: Long, title: String, fragment: Fragment) {
        fragmentIdList.add(id)
        fragmentTitleList.add(title)
        fragmentList.add(fragment)
    }

    fun addFragmentToStart(id: Long, title: String, fragment: Fragment) {
        fragmentIdList.add(0, id)
        fragmentTitleList.add(0, title)
        fragmentList.add(0, fragment)
    }

    fun clear() {
        fragmentTitleList.clear()
        fragmentList.clear()
    }

    override fun getItemId(position: Int): Long = fragmentIdList[position]
}
