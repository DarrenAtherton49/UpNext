package com.atherton.tmdb.ui.features.discover

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.atherton.tmdb.R
import com.atherton.tmdb.ui.main.MainViewModel
import com.atherton.tmdb.util.base.BaseFragment
import com.atherton.tmdb.util.extensions.getActivityViewModel
import com.atherton.tmdb.util.extensions.getAppComponent
import javax.inject.Inject

class DiscoverFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_discover

    @Inject lateinit var vmFactory: ViewModelProvider.Factory
    private lateinit var discoverViewModel: DiscoverViewModel
    private lateinit var mainViewModel: MainViewModel
    //private lateinit var recyclerAdapter: ScheduleAdapter //todo

    //@Inject lateinit var api: TmdbSearchService

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //scheduleViewModel = getViewModel(vmFactory, DailyListViewModel::class.java) //todo
        mainViewModel = getActivityViewModel(vmFactory, MainViewModel::class.java)

        observeViewModels()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    //todo
    private fun initRecyclerView() {
//        recyclerAdapter = DailyAdapter { item ->
//            dailyViewModel.itemClicked(item)
//        }
//        recyclerView.apply {
//            adapter = recyclerAdapter
//            layoutManager = LinearLayoutManager(context)
//        }
    }

    private fun observeViewModels() {

    }

    override fun initInjection() {
        DaggerDiscoverComponent.builder()
                .appComponent(getAppComponent())
                .build()
                .inject(this)
    }

    companion object {
        fun newInstance() = DiscoverFragment()
    }
}
