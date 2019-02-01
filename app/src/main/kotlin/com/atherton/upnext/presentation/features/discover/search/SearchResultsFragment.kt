package com.atherton.upnext.presentation.features.discover.search

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.data.model.Movie
import com.atherton.upnext.data.model.Person
import com.atherton.upnext.data.model.TvShow
import com.atherton.upnext.presentation.features.discover.DaggerDiscoverComponent
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.showSoftKeyboard
import com.atherton.upnext.util.recyclerview.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.base_recycler_view.*
import kotlinx.android.synthetic.main.search_results_search_field.*
import javax.inject.Inject

class SearchResultsFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_search_results

    @Inject lateinit var vmFactory: ViewModelProvider.Factory
    private lateinit var activityViewModel: MainViewModel
    private lateinit var recyclerViewAdapter: SearchResultsAdapter

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

    private fun initRecyclerView() {
        recyclerViewAdapter = SearchResultsAdapter {
            //todo dispatch click action to viewmodel/MVI
        }
        recyclerView.apply {
            adapter = recyclerViewAdapter
            layoutManager = GridLayoutManager(context, GRID_NUM_COLUMNS)
            addItemDecoration(
                GridSpacingItemDecoration(
                    GRID_NUM_COLUMNS,
                    resources.getDimensionPixelSize(R.dimen.search_results_grid_spacing)
                )
            )
        }
        //todo remove sample data
        recyclerViewAdapter.submitList(
            listOf(
                Movie(
                    true,
                    "",
                    listOf(),
                    0,
                    "",
                    "",
                    "",
                    0f,
                    "",
                    "",
                    "",
                    true,
                    2.1f,
                    1
                ),
                TvShow(
                    "",
                    "",
                    listOf(),
                    1,
                    "",
                    listOf(),
                    "",
                    "",
                    "",
                    "",
                    2.0f,
                    2.0f,
                    1
                ),
                Person(
                    true,
                    1,
                    listOf(),
                    "",
                    1.0f,
                    ""
                )
            )
        )
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

        private const val GRID_NUM_COLUMNS = 3
    }
}
