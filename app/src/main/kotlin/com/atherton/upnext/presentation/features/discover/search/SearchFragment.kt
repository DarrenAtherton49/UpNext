package com.atherton.upnext.presentation.features.discover.search

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModelViewMode
import com.atherton.upnext.presentation.common.SearchModelAdapter
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.*
import com.atherton.upnext.util.glide.GlideApp
import com.atherton.upnext.util.recyclerview.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.base_recycler_view.*
import kotlinx.android.synthetic.main.discover_error_layout.*
import kotlinx.android.synthetic.main.search_results_search_field.*
import javax.inject.Inject
import javax.inject.Named

class SearchFragment
    : BaseFragment<SearchAction, SearchState, SearchViewEffect, SearchViewModel>() {

    override val layoutResId: Int = R.layout.fragment_search
    override val stateBundleKey: String = "bundle_key_search_results_state"

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(SearchViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    override val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: SearchViewModel by lazy {
        getViewModel<SearchViewModel>(vmFactory)
    }
    private val recyclerViewAdapter: SearchModelAdapter by lazy {
        //todo make view mode toggleable
        SearchModelAdapter(GlideApp.with(this), SearchModelViewMode.Grid) { searchModel ->
            viewModel.dispatch(SearchAction.SearchResultClicked(searchModel))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo only show this if we are in search results mode and not advanced search results mode as the search field won't be there
        //todo check the MVI state to see if it is !detaching
        searchEditText.showSoftKeyboard()

        // load popular on first launch
        if (savedInstanceState == null) {
            viewModel.dispatch(SearchAction.SearchTextChanged(""))
        }

        //todo when fragment goes away, we need to hide the keyboard (could do this as part of the MVI state or an view effect?)

        retryButton.setOnClickListener {
            viewModel.dispatch(SearchAction.RetryButtonClicked(searchEditText.text.toString()))
        }

        initRecyclerView()
    }

    override fun onResume() {
        super.onResume()
        searchEditText.whenTextChanges {
            viewModel.dispatch(SearchAction.SearchTextChanged(it))
        }
    }

    override fun renderState(state: SearchState) {
        when (state) {
            is SearchState.Loading -> {
                progressBar.isVisible = true
                recyclerView.isVisible = false
                errorLayout.isVisible = false
            }
            is SearchState.Content -> {
                progressBar.isVisible = false
                if (state.results.isEmpty()) {
                    recyclerView.isVisible = false
                    errorLayout.isVisible = true
                    errorTextView.text = getString(R.string.search_error_network_try_again)
                } else {
                    recyclerView.isVisible = true
                    errorLayout.isVisible = false
                    recyclerViewAdapter.submitList(state.results)
                }
            }
            is SearchState.Error -> {
                progressBar.isVisible = false
                recyclerView.isVisible = false
                errorLayout.isVisible = true
                errorTextView.text = state.failure.generateErrorMessage(requireContext())
                retryButton.isVisible = state.failure is Response.Failure.NetworkError
            }
        }
    }

    override fun processViewEffects(viewEffect: SearchViewEffect) {
        //todo
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            setHasFixedSize(true)
            adapter = recyclerViewAdapter
            val numColumns = resources.getInteger(R.integer.search_model_grid_num_columns)
            layoutManager = GridLayoutManager(context, numColumns)
            addItemDecoration(
                GridSpacingItemDecoration(
                    numColumns = numColumns,
                    spacingInPixels = resources.getDimensionPixelSize(R.dimen.search_model_grid_spacing)
                )
            )
        }
    }

    override fun initInjection(initialState: SearchState?) {
        DaggerSearchComponent.builder()
            .searchModule(SearchModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }
}
