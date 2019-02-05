package com.atherton.upnext.presentation.features.discover.search

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.data.repository.Response
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.*
import com.atherton.upnext.util.recyclerview.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.base_recycler_view.*
import kotlinx.android.synthetic.main.search_results_search_field.*
import javax.inject.Inject
import javax.inject.Named

class SearchResultsFragment : BaseFragment<SearchResultsAction, SearchResultsState, SearchResultsViewModel>() {

    override val layoutResId: Int = R.layout.fragment_search_results
    override val stateBundleKey: String = "bundle_key_search_results_state"

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(SearchResultsViewModelFactory.NAME)
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

        // load popular on first launch
        if (savedInstanceState == null) {
            viewModel.dispatch(SearchResultsAction.SearchTextChanged(""))
        }

        //todo when fragment goes away, we need to hide the keyboard (could do this as part of the MVI state or an view effect?)

        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        searchEditText.whenTextChanges {
            viewModel.dispatch(SearchResultsAction.SearchTextChanged(it))
        }
    }

    override fun renderState(state: SearchResultsState) {
        when {
            state.results.isNotEmpty() -> {
                recyclerViewAdapter.submitList(state.results)
            }
            state.failure != null -> {
                val errorMessage: String = when (state.failure) {
                    is Response.Failure.AppError.Generic -> ""
                    is Response.Failure.AppError.NoResourcesFound -> "no results found"
                    is Response.Failure.ServerError -> ""
                    is Response.Failure.NetworkError -> ""
                }
                //todo show error layout
                //todo set error message
            }
        }
        progressBar.isVisible = state.isLoading
        recyclerView.isVisible = !state.results.isEmpty()
        //todo errorLayout.isVisible = state.failure != null
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
