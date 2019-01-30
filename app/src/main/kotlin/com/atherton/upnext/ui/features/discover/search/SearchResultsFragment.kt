package com.atherton.upnext.ui.features.discover.search

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.ui.features.discover.DaggerDiscoverComponent
import com.atherton.upnext.ui.main.MainViewModel
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.showSoftKeyboard
import kotlinx.android.synthetic.main.search_results_search_field.*
import javax.inject.Inject

class SearchResultsFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_search_results

    @Inject lateinit var vmFactory: ViewModelProvider.Factory
    private lateinit var activityViewModel: MainViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //viewModel = getViewModel(vmFactory, SearchResultsViewModel::class.java) //todo
        activityViewModel = getActivityViewModel(vmFactory, MainViewModel::class.java)

        observeViewModels()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo only show this if we are in search results mode and not advanced search results mode as the search field won't be there
        //todo check the MVI state to see if it is !detaching
        searchEditText.showSoftKeyboard()

        //todo when fragment goes away, we need to hide the keyboard (could do this as part of the MVI state or an event?)

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
        fun newInstance() = SearchResultsFragment()
    }
}
