package com.atherton.upnext.presentation.features.discover.search

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.*
import com.atherton.upnext.util.glide.GlideApp
import com.atherton.upnext.util.recyclerview.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.base_recycler_view.*
import kotlinx.android.synthetic.main.search_results_error_layout.*
import kotlinx.android.synthetic.main.search_results_search_field.*
import timber.log.Timber
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
        SearchResultsAdapter(GlideApp.with(this)) { searchModel ->
            viewModel.dispatch(SearchResultsAction.ResultClicked(searchModel))
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

        retryButton.setOnClickListener {
            viewModel.dispatch(SearchResultsAction.SearchTextChanged(searchEditText.text.toString()))
        }

        initRecyclerView()


        val s = "hello"
        when {
            s is String -> Timber.tag("darren").d("found1")
            s.equals("hello") -> Timber.tag("darren").d("found2")
        }
    }

    override fun onResume() {
        super.onResume()
        searchEditText.whenTextChanges {
            viewModel.dispatch(SearchResultsAction.SearchTextChanged(it))
        }
    }

    override fun renderState(state: SearchResultsState) {
        // it is important that we check these in this order, because a state can be successful (failure == null)
        // but still have no results (results.isEmpty()). If we were to check (results.isEmpty()) first, then we
        // would miss out on a potential failure (failure != null) because we can have a failure state that
        // also has an empty list. Might be better to move to a sealed class approach so separate this issue.
        when {
            state.failure != null -> errorTextView.text = generateErrorMessage(state.failure)
            state.results.isNotEmpty() -> recyclerViewAdapter.submitList(state.results)
            state.results.isEmpty() -> errorTextView.text = getString(R.string.search_results_error_network_try_again)
        }
        progressBar.isVisible = state.isLoading
        recyclerView.isVisible = state.results.isNotEmpty()
        errorLayout.isVisible = state.failure != null || state.results.isEmpty()
        retryButton.isVisible = state.failure != null && state.failure is Response.Failure.NetworkError
    }

    private fun generateErrorMessage(failure: Response.Failure): String {
        return when (failure) {
            is Response.Failure.ServerError -> {
                val apiError = failure.error
                if (apiError != null) {
                    getString(R.string.search_results_error_server_with_error).format(
                        failure.code,
                        apiError.statusCode
                    )
                } else {
                    getString(R.string.search_results_error_server).format(failure.code)
                }
            }
            is Response.Failure.NetworkError -> getString(R.string.search_results_error_network)
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
        private const val GRID_NUM_COLUMNS = 3
    }
}
