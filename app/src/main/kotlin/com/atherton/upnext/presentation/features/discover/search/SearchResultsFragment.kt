package com.atherton.upnext.presentation.features.discover.search

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.getViewModel
import com.atherton.upnext.util.extensions.showSoftKeyboard
import com.atherton.upnext.util.recyclerview.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.base_recycler_view.*
import kotlinx.android.synthetic.main.search_results_search_field.*
import javax.inject.Inject
import javax.inject.Named

class SearchResultsFragment : BaseFragment<SearchResultsAction, SearchResultsState, SearchResultsViewModel>() {

    override val layoutResId: Int = R.layout.fragment_search_results
    override val stateBundleKey: String = "bundle_key_search_results_state"

    @field:[Inject Named(MainViewModelFactory.NAME)]
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @field:[Inject Named(SearchResultsViewModelFactory.NAME)]
    lateinit var vmFactory: ViewModelProvider.Factory

    private val activityViewModel: MainViewModel by lazy {
        getActivityViewModel<MainViewModel>(mainVmFactory)

    }
    override val viewModel: SearchResultsViewModel by lazy {
        getViewModel<SearchResultsViewModel>(vmFactory)
    }
    private val recyclerViewAdapter: SearchResultsAdapter by lazy {
        SearchResultsAdapter {
            viewModel.dispatch(SearchResultsAction.ResultClicked) //todo add search result item as parameter
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo only show this if we are in search results mode and not advanced search results mode as the search field won't be there
        //todo check the MVI state to see if it is !detaching
        searchEditText.showSoftKeyboard()

        //todo when fragment goes away, we need to hide the keyboard (could do this as part of the MVI state or an view effect?)

        initRecyclerView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.dispatch(SearchResultsAction.LoadPopular)
    }

    override fun renderState(state: SearchResultsState) {
        when {
            state.isLoading -> {

            }
            state.failure != null -> {

            }
            else -> {

            }
        }
    }

    private fun initRecyclerView() {
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
    }

    override fun initInjection(initialState: SearchResultsState?) {
        DaggerSearchResultsComponent.builder()
            .searchModule(SearchModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }

    companion object {
        fun newInstance() = SearchResultsFragment()

        private const val GRID_NUM_COLUMNS = 3
    }
}
