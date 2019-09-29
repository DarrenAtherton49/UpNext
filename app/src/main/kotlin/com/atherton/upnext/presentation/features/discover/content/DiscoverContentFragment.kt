package com.atherton.upnext.presentation.features.discover.content

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.DiscoverFilter
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
import com.atherton.upnext.util.extension.getActivityViewModel
import com.atherton.upnext.util.extension.getAppComponent
import com.atherton.upnext.util.extension.getViewModel
import com.atherton.upnext.util.extension.isVisible
import kotlinx.android.synthetic.main.error_retry_layout.*
import kotlinx.android.synthetic.main.fragment_discover_content.*
import javax.inject.Inject
import javax.inject.Named


class DiscoverContentFragment
    : BaseFragment<DiscoverContentAction, DiscoverContentState, DiscoverContentViewEffect, DiscoverContentViewModel>() {

    override val layoutResId: Int = R.layout.fragment_discover_content
    override val stateBundleKey: String by lazy { "bundle_key_discover_content_${filter.id}_state" }
    private val filter: DiscoverFilter by lazy { arguments?.getParcelable(BUNDLE_KEY_FILTER) as DiscoverFilter }

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(DiscoverContentViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    override val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: DiscoverContentViewModel by lazy { getViewModel<DiscoverContentViewModel>(vmFactory) }

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

    override val toolbarOptions: ToolbarOptions? = null

    @Inject lateinit var imageLoader: ImageLoader

    private val recyclerViewAdapter: SearchModelAdapter by lazy {
        SearchModelAdapter(imageLoader, GlideApp.with(this)) { searchModel ->
            viewModel.dispatch(DiscoverContentAction.SearchModelClicked(searchModel))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.adapter = recyclerViewAdapter

        retryButton.setOnClickListener {
            viewModel.dispatch(DiscoverContentAction.RetryButtonClicked(filter))
        }

        if (savedInstanceState == null) {
            viewModel.dispatch(DiscoverContentAction.Load(null, filter))
        }
    }

    override fun onMenuItemClicked(menuItem: MenuItem): Boolean = false

    override fun renderState(state: DiscoverContentState) {
        when (state) {
            is DiscoverContentState.Loading -> {
                errorLayout.isVisible = false
                progressBar.isVisible = true

                if (state.results != null && state.results.isNotEmpty() && state.viewMode != null) {
                    // show a loading state with cached data
                    recyclerView.isVisible = true
                    initRecyclerView(state.viewMode)
                    recyclerViewAdapter.submitList(state.results)
                } else {
                    recyclerView.isVisible = false
                }
            }
            is DiscoverContentState.Content -> {
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
            is DiscoverContentState.Error -> {

                progressBar.isVisible = false

                if (state.fallbackResults != null && state.fallbackResults.isNotEmpty()) {
                    recyclerView.isVisible = true
                    recyclerViewAdapter.submitList(state.fallbackResults)
                    //todo show device is offline/data is stale message?
                } else {
                    recyclerView.isVisible = false
                    errorLayout.isVisible = true
                    errorTextView.text = state.message
                    retryButton.isVisible = state.canRetry
                }
            }
        }
    }

    override fun processViewEffects(viewEffect: DiscoverContentViewEffect) {
        when (viewEffect) {
            is DiscoverContentViewEffect.ShowTvShowDetailScreen -> {
                sharedViewModel.dispatch(MainAction.TvShowClicked(viewEffect.tvShowId))
            }
            is DiscoverContentViewEffect.ShowMovieDetailScreen -> {
                sharedViewModel.dispatch(MainAction.MovieClicked(viewEffect.movieId))
            }
            is DiscoverContentViewEffect.ShowPersonDetailScreen -> {
                sharedViewModel.dispatch(MainAction.PersonClicked(viewEffect.personId))
            }
        }
    }

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {
        when (viewEffect) {
            // view mode has been changed elsewhere (i.e. in the fragment containing the tabs), reload view with new setting
            is MainViewEffect.ToggleViewMode -> {
                viewModel.dispatch(DiscoverContentAction.Load(viewEffect.viewMode, filter))
            }
        }
    }

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

    override fun initInjection(initialState: DiscoverContentState?) {
        DaggerDiscoverContentComponent.builder()
            .discoverContentModule(DiscoverContentModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }

    companion object {
        private const val BUNDLE_KEY_FILTER = "discover_content_bundle_key_filter"

        fun newInstance(filter: DiscoverFilter): DiscoverContentFragment {
            return DiscoverContentFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BUNDLE_KEY_FILTER, filter)
                }
            }
        }
    }
}
