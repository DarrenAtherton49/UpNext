package com.atherton.upnext.presentation.features.discover.featured

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.domain.model.SearchModelViewMode
import com.atherton.upnext.presentation.common.SearchModelAdapter
import com.atherton.upnext.presentation.main.MainAction
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.*
import com.atherton.upnext.util.glide.GlideApp
import com.atherton.upnext.util.recyclerview.GridSpacingItemDecoration
import com.atherton.upnext.util.recyclerview.LinearSpacingItemDecoration
import kotlinx.android.synthetic.main.base_recycler_view.*
import kotlinx.android.synthetic.main.discover_error_layout.*
import kotlinx.android.synthetic.main.fragment_discover.*
import javax.inject.Inject
import javax.inject.Named


class DiscoverFragment : BaseFragment<DiscoverAction, DiscoverState, DiscoverViewEffect, DiscoverViewModel>() {

    override val layoutResId: Int = com.atherton.upnext.R.layout.fragment_discover
    override val stateBundleKey: String = "bundle_key_discover_state"

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(DiscoverViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    private val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: DiscoverViewModel by lazy { getViewModel<DiscoverViewModel>(vmFactory) }
    private val navController: NavController by lazy { findNavController() }

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

    private lateinit var recyclerViewAdapter: SearchModelAdapter

    private var viewMode: SearchModelViewMode? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.dispatch(DiscoverAction.Load)

        retryButton.setOnClickListener {
            viewModel.dispatch(DiscoverAction.RetryButtonClicked)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (menu != null && inflater != null) {
            inflater.inflate(R.menu.menu_discover, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        viewMode?.let {
            val viewToggleLogo = when (it) {
                is SearchModelViewMode.List -> context?.getDrawableCompat(R.drawable.ic_view_grid_white_24dp)
                is SearchModelViewMode.Grid -> context?.getDrawableCompat(R.drawable.ic_view_list_white_24dp)
            }
            val menuItem = menu?.findItem(R.id.action_toggle_view)
            menuItem?.isVisible = true
            menuItem?.icon = viewToggleLogo
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                sharedViewModel.dispatch(MainAction.SearchActionClicked)
                true
            }
            R.id.action_toggle_view -> {
                viewModel.dispatch(DiscoverAction.ViewModeToggleActionClicked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun renderState(state: DiscoverState) {
        when (state) {
            is DiscoverState.Loading -> {
                progressBar.isVisible = true
                recyclerView.isVisible = false
                errorLayout.isVisible = false
            }
            is DiscoverState.Content -> {
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
            is DiscoverState.Error -> {
                progressBar.isVisible = false
                recyclerView.isVisible = false
                errorLayout.isVisible = true
                errorTextView.text = state.failure.generateErrorMessage(requireContext())
                retryButton.isVisible = state.failure is Response.Failure.NetworkError
            }
        }
    }

    override fun processViewEffects(viewEffect: DiscoverViewEffect) {
        when (viewEffect) {
            is DiscoverViewEffect.ToggleViewMode -> {
                viewMode = viewEffect.viewMode // save value for onPrepareOptionsMenu()
                activity?.invalidateOptionsMenu()
            }
            is DiscoverViewEffect.ShowSearchModelDetailScreen -> {
                //todo
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
                viewModel.dispatch(DiscoverAction.SearchModelClicked(searchModel))
            }
            adapter = recyclerViewAdapter
        }
    }

    override fun initInjection(initialState: DiscoverState?) {
        DaggerDiscoverComponent.builder()
            .discoverModule(DiscoverModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }
}
