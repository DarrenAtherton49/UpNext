package com.atherton.upnext.presentation.features.discover.content

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
import kotlinx.android.synthetic.main.discover_error_layout.*
import kotlinx.android.synthetic.main.fragment_discover_content.*
import javax.inject.Inject
import javax.inject.Named


class DiscoverContentFragment
    : BaseFragment<DiscoverContentAction, DiscoverContentState, DiscoverContentViewEffect, DiscoverContentViewModel>() {

    override val layoutResId: Int = com.atherton.upnext.R.layout.fragment_discover_content
    override val stateBundleKey: String by lazy { "bundle_key_discover_content_${filter}_state" }
    private val filter: String by lazy { arguments?.getString(BUNDLE_KEY_FILTER) ?: "unknown" }

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

    private lateinit var recyclerViewAdapter: SearchModelAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        retryButton.setOnClickListener {
            viewModel.dispatch(DiscoverContentAction.RetryButtonClicked)
        }
    }

    override fun onMenuItemClicked(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_search -> {
                sharedViewModel.dispatch(MainAction.SearchActionClicked)
                true
            }
            else -> false
        }
    }

    override fun renderState(state: DiscoverContentState) {
        when (state) {
            is DiscoverContentState.Loading -> {
                progressBar.isVisible = true
                recyclerView.isVisible = false
                errorLayout.isVisible = false
            }
            is DiscoverContentState.Content -> {
                progressBar.isVisible = false
                if (state.results.isEmpty()) {
                    recyclerView.isVisible = false
                    errorLayout.isVisible = true
                    errorTextView.text = getString(R.string.search_error_network_try_again)
                } else {
                    recyclerView.isVisible = true
                    errorLayout.isVisible = false
                    initRecyclerView(state.viewMode)
                    recyclerViewAdapter.submitList(state.results)
                }
            }
            is DiscoverContentState.Error -> {
                progressBar.isVisible = false
                recyclerView.isVisible = false
                errorLayout.isVisible = true
                errorTextView.text = state.failure.generateErrorMessage(requireContext())
                retryButton.isVisible = state.failure is Response.Failure.NetworkError
            }
        }
    }

    override fun processViewEffects(viewEffect: DiscoverContentViewEffect) {}

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {
        when (viewEffect) {
            // view mode has been changed elsewhere (i.e. in the fragment containing the tabs), reload view with new setting
            is MainViewEffect.ToggleViewMode -> {
                viewModel.dispatch(DiscoverContentAction.ViewModeToggleChanged(viewEffect.viewMode))
            }
        }
    }

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
                viewModel.dispatch(DiscoverContentAction.SearchModelClicked(searchModel))
            }
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

        //todo change filter to a proper type instead of a string (e.g. a sealed class)
        fun newInstance(filter: String): DiscoverContentFragment {
            return DiscoverContentFragment().apply {
                arguments = Bundle().apply {
                    putString(BUNDLE_KEY_FILTER, filter)
                }
            }
        }
    }
}
