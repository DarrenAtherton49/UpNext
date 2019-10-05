package com.atherton.upnext.presentation.features.shows.content

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.ContentList
import com.atherton.upnext.presentation.base.BaseFragment
import com.atherton.upnext.presentation.common.ContentType
import com.atherton.upnext.presentation.main.MainAction
import com.atherton.upnext.presentation.main.MainViewEffect
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.presentation.util.glide.GlideApp
import com.atherton.upnext.presentation.util.image.ImageLoader
import com.atherton.upnext.presentation.util.recyclerview.LinearSpacingItemDecoration
import com.atherton.upnext.presentation.util.toolbar.ToolbarOptions
import com.atherton.upnext.util.extension.*
import kotlinx.android.synthetic.main.error_retry_layout.*
import kotlinx.android.synthetic.main.fragment_content_list.*
import javax.inject.Inject
import javax.inject.Named

class ShowListFragment : BaseFragment<ShowListAction, ShowListState, ShowListViewEffect, ShowListViewModel>() {

    override val layoutResId: Int = R.layout.fragment_content_list
    override val stateBundleKey: String by lazy { "bundle_key_show_list_${showList.id}_state" }
    private val showList: ContentList by lazy { arguments?.getParcelable(BUNDLE_KEY_SHOW_LIST) as ContentList }

    @Inject
    @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject
    @field:Named(ShowListViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    override val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: ShowListViewModel by lazy { getViewModel<ShowListViewModel>(vmFactory) }

    override val toolbarOptions: ToolbarOptions? = null

    @Inject lateinit var imageLoader: ImageLoader

    private val showListItemDecoration: LinearSpacingItemDecoration by lazy {
        LinearSpacingItemDecoration(
            spacingInPixels = resources.getDimensionPixelSize(R.dimen.content_list_spacing),
            orientation = LinearSpacingItemDecoration.Orientation.Vertical
        )
    }

    private val recyclerViewAdapter: ShowListAdapter by lazy {
        ShowListAdapter(
            imageLoader = imageLoader,
            glideRequests = GlideApp.with(this),
            onItemClickListener = { showListItem ->
                viewModel.dispatch(ShowListAction.ShowClicked(showListItem.showId))
            },
            onWatchlistButtonClickListener = { showListItem ->
                viewModel.dispatch(ShowListAction.ToggleWatchlistButtonClicked(showList, showListItem.showId))
            },
            onWatchedButtonClickListener = { showListItem ->
                viewModel.dispatch(ShowListAction.ToggleWatchedButtonClicked(showList, showListItem.showId))
            },
            onAddToListClickListener = { showListItem ->
                viewModel.dispatch(ShowListAction.AddToListButtonClicked(showListItem.showId))
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        retryButton.setOnClickListener {
            viewModel.dispatch(ShowListAction.RetryButtonClicked(showList))
        }

        if (savedInstanceState == null) {
            viewModel.dispatch(ShowListAction.Load(showList))
        }
    }

    override fun onMenuItemClicked(menuItem: MenuItem): Boolean = false

    override fun renderState(state: ShowListState) {
        when (state) {
            is ShowListState.Loading -> {
                errorLayout.isVisible = false
                progressBar.isVisible = true

                if (state.results != null && state.results.isNotEmpty()) {
                    // show a loading state with cached data
                    recyclerView.isVisible = true
                    recyclerViewAdapter.submitList(state.results)
                } else {
                    recyclerView.isVisible = false
                }
            }
            is ShowListState.Content -> {
                progressBar.isVisible = false
                if (state.results.isEmpty()) {
                    recyclerView.isVisible = false
                    errorLayout.isVisible = true
                    errorTextView.text = getString(R.string.error_no_results_found)
                } else {
                    recyclerView.isVisible = true
                    errorLayout.isVisible = false
                    recyclerViewAdapter.submitList(state.results)
                }
            }
            is ShowListState.Error -> {

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

    override fun processViewEffects(viewEffect: ShowListViewEffect) {
        when (viewEffect) {
            is ShowListViewEffect.ShowDetailScreen -> {
                sharedViewModel.dispatch(MainAction.TvShowClicked(viewEffect.showId))
            }
            is ShowListViewEffect.ShowAddToListMenu -> {
                navigator.showAddToListsMenu(
                    contentId = viewEffect.showId,
                    contentType = ContentType.TvShow,
                    fragmentManager = childFragmentManager
                )
            }
            is ShowListViewEffect.ShowRemovedFromListMessage -> {
                showRemovedFromListMessage(viewEffect.message, viewEffect.showId)
            }
        }
    }

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {}

    private fun showRemovedFromListMessage(message: String, showId: Long) {
        contentListCoordinatorLayout.showLongSnackbar(
            text = message,
            actionText = getString(R.string.generic_action_undo),
            onClick = {
                viewModel.dispatch(ShowListAction.ToggleWatchlistButtonClicked(showList, showId))
            }
        )
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            setHasFixedSize(true)
            if (itemDecorationCount > 0) {
                removeItemDecorationAt(0)
            }
            addItemDecoration(showListItemDecoration)
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
        }
    }

    override fun initInjection(initialState: ShowListState?) {
        DaggerShowListComponent.builder()
            .showListModule(ShowListModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }

    companion object {
        private const val BUNDLE_KEY_SHOW_LIST = "bundle_key_show_list_items"

        fun newInstance(showList: ContentList): ShowListFragment {
            return ShowListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BUNDLE_KEY_SHOW_LIST, showList)
                }
            }
        }
    }
}
