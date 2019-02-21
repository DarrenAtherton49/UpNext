package com.atherton.upnext.presentation.features.search

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModelViewMode
import com.atherton.upnext.presentation.common.SearchModelAdapter
import com.atherton.upnext.presentation.main.MainAction
import com.atherton.upnext.presentation.main.MainViewEffect
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.base.ToolbarOptions
import com.atherton.upnext.util.extensions.*
import com.atherton.upnext.util.glide.GlideApp
import com.atherton.upnext.util.recyclerview.GridSpacingItemDecoration
import com.atherton.upnext.util.recyclerview.LinearSpacingItemDecoration
import kotlinx.android.synthetic.main.error_retry_layout.*
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.search_results_search_field.*
import javax.inject.Inject
import javax.inject.Named

class SearchFragment : BaseFragment<SearchAction, SearchState, SearchViewEffect, SearchViewModel>() {

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

    private val listItemDecoration: LinearSpacingItemDecoration by lazy {
        LinearSpacingItemDecoration(
            spacingInPixels = resources.getDimensionPixelSize(R.dimen.search_model_list_spacing),
            orientation = LinearSpacingItemDecoration.Orientation.Vertical
        )
    }

    private val gridItemDecoration: GridSpacingItemDecoration by lazy {
        GridSpacingItemDecoration(
            numColumns = resources.getInteger(R.integer.search_model_grid_num_columns),
            spacingInPixels = resources.getDimensionPixelSize(R.dimen.search_model_grid_spacing)
        )
    }

    override val toolbarOptions: ToolbarOptions? = ToolbarOptions(
        toolbarResId = R.id.toolbar,
        titleResId = R.string.fragment_label_search,
        menuResId = R.menu.menu_search
    )

    private lateinit var recyclerViewAdapter: SearchModelAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchEditText.showSoftKeyboard()

        retryButton.setOnClickListener {
            viewModel.dispatch(SearchAction.RetryButtonClicked(searchEditText.text.toString()))
        }

        // load popular on first launch
        if (savedInstanceState == null) {
            viewModel.dispatch(SearchAction.SearchTextChanged(""))
        }

        viewModel.dispatch(SearchAction.LoadViewMode)
    }

    override fun onResume() {
        super.onResume()
        searchEditText.whenTextChanges {
            viewModel.dispatch(SearchAction.SearchTextChanged(it))
        }
    }

    override fun onDestroyView() {
        searchEditText.hideSoftKeyboard()
        super.onDestroyView()
    }

    override fun onMenuItemClicked(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_settings -> {
                //todo dispatch action to open settings
                true
            }
            R.id.action_toggle_view -> {
                viewModel.dispatch(SearchAction.ViewModeToggleActionClicked(searchEditText.text.toString()))
                true
            }
            else -> false
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
                    errorTextView.text = getString(R.string.error_no_results_found)
                } else {
                    recyclerView.isVisible = true
                    errorLayout.isVisible = false
                    initRecyclerView(state.viewMode)
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
        when (viewEffect) {
            is SearchViewEffect.ToggleViewMode -> {
                editMenuItem(R.id.action_toggle_view) {
                    isVisible = true
                    icon = when (viewEffect.viewMode) {
                        is SearchModelViewMode.List -> context?.getDrawableCompat(R.drawable.ic_view_grid_white_24dp)
                        is SearchModelViewMode.Grid -> context?.getDrawableCompat(R.drawable.ic_view_list_white_24dp)
                    }
                }
            }
            is SearchViewEffect.ShowSearchModelDetailScreen -> {
                sharedViewModel.dispatch(MainAction.SearchModelClicked(viewEffect.searchModel))
            }
        }
    }

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {}

    private fun initRecyclerView(viewMode: SearchModelViewMode) {
        recyclerView.apply {
            setHasFixedSize(true)
            if (itemDecorationCount > 0) {
                removeItemDecorationAt(0)
            }
            when (viewMode) {
                is SearchModelViewMode.Grid -> {
                    addItemDecoration(gridItemDecoration)
                    val numColumns = resources.getInteger(R.integer.search_model_grid_num_columns)
                    layoutManager = GridLayoutManager(context, numColumns)
                }
                is SearchModelViewMode.List -> {
                    addItemDecoration(listItemDecoration)
                    layoutManager = LinearLayoutManager(context)
                }
            }
            recyclerViewAdapter = SearchModelAdapter(GlideApp.with(this), viewMode) { searchModel ->
                viewModel.dispatch(SearchAction.SearchResultClicked(searchModel))
            }
            adapter = recyclerViewAdapter
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
