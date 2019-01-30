package com.atherton.tmdb.ui.features.discover.featured

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.atherton.tmdb.R
import com.atherton.tmdb.data.api.TmdbSearchService
import com.atherton.tmdb.ui.features.discover.DaggerDiscoverComponent
import com.atherton.tmdb.ui.main.MainViewModel
import com.atherton.tmdb.util.base.BaseFragment
import com.atherton.tmdb.util.extensions.getActivityViewModel
import com.atherton.tmdb.util.extensions.getAppComponent
import kotlinx.android.synthetic.main.discover_search_field.*
import javax.inject.Inject

class DiscoverFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_discover

    @Inject lateinit var vmFactory: ViewModelProvider.Factory
    private lateinit var viewModel: DiscoverViewModel
    private lateinit var activityViewModel: MainViewModel
    //private lateinit var recyclerAdapter: ScheduleAdapter //todo

    @Inject lateinit var api: TmdbSearchService

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //viewModel = getViewModel(vmFactory, DiscoverViewModel::class.java) //todo
        activityViewModel = getActivityViewModel(vmFactory, MainViewModel::class.java)

        observeViewModels()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

        searchEditText.setOnClickListener {
            //todo dispatch action to viewmodel to say 'search edit text clicked'
            findNavController().navigate(R.id.actionGoToSearchResults)
        }
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
