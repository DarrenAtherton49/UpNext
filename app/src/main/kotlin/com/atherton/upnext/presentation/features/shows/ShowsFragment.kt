package com.atherton.upnext.presentation.features.shows


import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.getViewModel
import javax.inject.Inject
import javax.inject.Named

class ShowsFragment : BaseFragment<ShowsAction, ShowsState, ShowsViewModel>() {

    override val layoutResId: Int = R.layout.fragment_shows
    override val stateBundleKey: String = "bundle_key_shows_state"

    @Inject lateinit var viewPagerAdapter: ShowsViewPagerAdapter

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(ShowsViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    private val activityViewModel: MainViewModel by lazy {
        getActivityViewModel<MainViewModel>(mainVmFactory)

    }
    override val viewModel: ShowsViewModel by lazy {
        getViewModel<ShowsViewModel>(vmFactory)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
    }

    override fun renderState(state: ShowsState) {

    }

    private fun initViewPager() {
        //todo
//        viewPagerAdapter.addFragment(getString(R.string.shows_tab_up_next), )
//        viewPagerAdapter.addFragment(getString(R.string.shows_tab_watchlist), )
//        viewPagerAdapter.addFragment(getString(R.string.shows_tab_finished), )
//        viewPager.adapter = viewPagerAdapter
//        tabLayout.setupWithViewPager(viewPager)
    }

    override fun initInjection(initialState: ShowsState?) {
        DaggerShowsComponent.builder()
            .showsModule(ShowsModule(initialState, childFragmentManager))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }
}
