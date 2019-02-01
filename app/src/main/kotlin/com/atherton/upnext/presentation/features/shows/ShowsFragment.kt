package com.atherton.upnext.presentation.features.shows


import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import javax.inject.Inject

class ShowsFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_shows

    @Inject lateinit var vmFactory: ViewModelProvider.Factory
    private val activityViewModel: MainViewModel by lazy {
        getActivityViewModel(vmFactory, MainViewModel::class.java)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeViewModels()
    }

    private fun observeViewModels() {

    }

    override fun initInjection() {
        DaggerShowsComponent.builder()
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }

    companion object {
        fun newInstance() = ShowsFragment()
    }
}
