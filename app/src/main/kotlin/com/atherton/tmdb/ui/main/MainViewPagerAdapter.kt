package com.atherton.tmdb.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import java.util.*
import javax.inject.Inject

class MainViewPagerAdapter @Inject constructor(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val fragmentsList: MutableList<Pair<String, Fragment>> = ArrayList()

    override fun getItem(position: Int): Fragment = fragmentsList[position].second

    override fun getCount(): Int = fragmentsList.size

    override fun getPageTitle(position: Int): CharSequence? = fragmentsList[position].first

    operator fun set(title: String, fragment: Fragment) {
        fragmentsList.add(title to fragment)
    }
}