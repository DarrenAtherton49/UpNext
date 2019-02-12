package com.atherton.upnext.presentation.features.shows

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.atherton.upnext.util.injection.PerView
import java.util.*
import javax.inject.Inject

@PerView
class ShowsViewPagerAdapter @Inject constructor(
    fragmentManager: FragmentManager
) : FragmentPagerAdapter(fragmentManager) {

    private val fragmentTitleList: MutableList<String> = ArrayList()
    private val fragmentList: MutableList<Fragment> = ArrayList()

    override fun getItem(position: Int): Fragment = fragmentList[position]

    override fun getCount(): Int = fragmentList.count()

    fun addFragment(fragmentTitle: String, fragment: Fragment) {
        fragmentTitleList.add(fragmentTitle)
        fragmentList.add(fragment)
    }

    override fun getPageTitle(position: Int): CharSequence = fragmentTitleList[position]
}
