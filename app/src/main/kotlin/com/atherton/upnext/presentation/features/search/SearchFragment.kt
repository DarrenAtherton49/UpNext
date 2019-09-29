package com.atherton.upnext.presentation.features.search

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.GridViewMode
import com.atherton.upnext.presentation.base.BaseFragment
import com.atherton.upnext.presentation.common.searchmodel.SearchModelAdapter
import com.atherton.upnext.presentation.main.MainAction
import com.atherton.upnext.presentation.main.MainViewEffect
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.presentation.util.glide.GlideApp
import com.atherton.upnext.presentation.util.image.ImageLoader
import com.atherton.upnext.presentation.util.recyclerview.GridSpacingItemDecoration
import com.atherton.upnext.presentation.util.recyclerview.LinearSpacingItemDecoration
import com.atherton.upnext.presentation.util.toolbar.ToolbarOptions
import com.atherton.upnext.util.extension.*
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

    @Inject lateinit var imageLoader: ImageLoader

    private val recyclerViewAdapter: SearchModelAdapter by lazy {
        SearchModelAdapter(imageLoader, GlideApp.with(this)) { searchModel ->
            viewModel.dispatch(SearchAction.SearchResultClicked(searchModel))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = recyclerViewAdapter

        searchEditText.showSoftKeyboard()

        retryButton.setOnClickListener {
            viewModel.dispatch(SearchAction.RetryButtonClicked(searchEditText.text.toString()))
        }

        viewModel.dispatch(SearchAction.LoadViewMode)
    }

    override fun onResume() {
        super.onResume()
        searchEditText.whenTextChanges(emitInitialValue = true) {
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
                viewModel.dispatch(SearchAction.SettingsActionClicked)
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
                errorLayout.isVisible = false
                progressBar.isVisible = true
                //todo add a progress bar to the search results section as well as the search field?

                if (state.results != null && state.results.isNotEmpty() && state.viewMode != null) {
                    // show a loading state with cached data
                    recyclerView.isVisible = true
                    initRecyclerView(state.viewMode)
                    recyclerViewAdapter.submitList(state.results)
                } else {
                    recyclerView.isVisible = false
                }
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

                if (state.fallbackResults != null && state.fallbackResults.isNotEmpty()) { // show cached data
                    recyclerView.isVisible = true
                    initRecyclerView(state.viewMode)
                    recyclerViewAdapter.submitList(state.fallbackResults)
                    //todo show device is offline message?
                } else {
                    recyclerView.isVisible = false
                    errorLayout.isVisible = true
                    errorTextView.text = state.message
                    retryButton.isVisible = state.canRetry
                }
            }
        }
    }

    override fun processViewEffects(viewEffect: SearchViewEffect) {
        when (viewEffect) {
            is SearchViewEffect.ToggleViewMode -> {
                editMenuItem(R.id.action_toggle_view) {
                    isVisible = true
                    icon = when (viewEffect.viewMode) {
                        is GridViewMode.List -> context?.getDrawableCompat(R.drawable.ic_view_grid_white_24dp)
                        is GridViewMode.Grid -> context?.getDrawableCompat(R.drawable.ic_view_list_white_24dp)
                    }
                }
            }
            is SearchViewEffect.ShowTvShowDetailScreen -> {
                sharedViewModel.dispatch(MainAction.TvShowClicked(viewEffect.tvShowId))
            }
            is SearchViewEffect.ShowMovieDetailScreen -> {
                sharedViewModel.dispatch(MainAction.MovieClicked(viewEffect.movieId))
            }
            is SearchViewEffect.ShowPersonDetailScreen -> {
                sharedViewModel.dispatch(MainAction.PersonClicked(viewEffect.personId))
            }
            is SearchViewEffect.ShowSettingsScreen -> {
                sharedViewModel.dispatch(MainAction.SettingsActionClicked)
            }
        }
    }

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {}

    private fun initRecyclerView(viewMode: GridViewMode) {
        recyclerView.apply {
            setHasFixedSize(true)
            if (itemDecorationCount > 0) {
                removeItemDecorationAt(0)
            }
            when (viewMode) {
                is GridViewMode.Grid -> {
                    addItemDecoration(gridItemDecoration)
                    val numColumns = resources.getInteger(R.integer.search_model_grid_num_columns)
                    layoutManager = GridLayoutManager(context, numColumns)
                }
                is GridViewMode.List -> {
                    addItemDecoration(listItemDecoration)
                    layoutManager = LinearLayoutManager(context)
                }
            }
            recyclerViewAdapter.viewMode = viewMode
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
