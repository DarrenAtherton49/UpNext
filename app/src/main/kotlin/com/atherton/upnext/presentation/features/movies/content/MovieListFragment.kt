package com.atherton.upnext.presentation.features.movies.content

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

class MovieListFragment : BaseFragment<MovieListAction, MovieListState, MovieListViewEffect, MovieListViewModel>() {

    override val layoutResId: Int = R.layout.fragment_content_list
    override val stateBundleKey: String by lazy { "bundle_key_movie_list_${movieList.id}_state" }
    private val movieList: ContentList by lazy { arguments?.getParcelable(BUNDLE_KEY_MOVIE_LIST) as ContentList }

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(MovieListViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    override val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: MovieListViewModel by lazy { getViewModel<MovieListViewModel>(vmFactory) }

    override val toolbarOptions: ToolbarOptions? = null

    @Inject lateinit var imageLoader: ImageLoader

    private val recyclerViewAdapter: MovieListAdapter by lazy {
        MovieListAdapter(
            imageLoader = imageLoader,
            glideRequests = GlideApp.with(this),
            onItemClickListener = { movieListItem ->
                viewModel.dispatch(MovieListAction.MovieClicked(movieListItem.movieId))
            },
            onWatchlistButtonClickListener = { movieListItem ->
                viewModel.dispatch(MovieListAction.ToggleWatchlistButtonClicked(movieList, movieListItem.movieId))
            },
            onWatchedButtonClickListener = { movieListItem ->
                viewModel.dispatch(MovieListAction.ToggleWatchedButtonClicked(movieList, movieListItem.movieId))
            },
            onAddToListClickListener = { movieListItem ->
                viewModel.dispatch(MovieListAction.AddToListButtonClicked(movieListItem.movieId))
            }
        )
    }

    private val movieListItemDecoration: LinearSpacingItemDecoration by lazy {
        LinearSpacingItemDecoration(
            spacingInPixels = resources.getDimensionPixelSize(R.dimen.content_list_spacing),
            orientation = LinearSpacingItemDecoration.Orientation.Vertical
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        retryButton.setOnClickListener {
            viewModel.dispatch(MovieListAction.RetryButtonClicked(movieList))
        }

        if (savedInstanceState == null) {
            viewModel.dispatch(MovieListAction.Load(movieList))
        }
    }

    override fun onMenuItemClicked(menuItem: MenuItem): Boolean = false

    override fun renderState(state: MovieListState) {
        when (state) {
            is MovieListState.Loading -> {
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
            is MovieListState.Content -> {
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
            is MovieListState.Error -> {

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

    override fun processViewEffects(viewEffect: MovieListViewEffect) {
        when (viewEffect) {
            is MovieListViewEffect.ShowMovieDetailScreen -> {
                sharedViewModel.dispatch(MainAction.MovieClicked(viewEffect.movieId))
            }
            is MovieListViewEffect.ShowAddToListMenu -> {
                navigator.showAddToListsMenu(
                    contentId = viewEffect.movieId,
                    contentType = ContentType.Movie,
                    fragmentManager = childFragmentManager
                )
            }
            is MovieListViewEffect.ShowRemovedFromListMessage -> {
                showMovieRemovedFromListMessage(viewEffect.message, viewEffect.movieId)
            }
        }
    }

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {}

    private fun showMovieRemovedFromListMessage(message: String, movieId: Long) {
        contentListCoordinatorLayout.showLongSnackbar(
            text = message,
            actionText = getString(R.string.generic_action_undo),
            onClick = {
                viewModel.dispatch(MovieListAction.ToggleWatchlistButtonClicked(movieList, movieId))
            }
        )
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            setHasFixedSize(true)
            if (itemDecorationCount > 0) {
                removeItemDecorationAt(0)
            }
            addItemDecoration(movieListItemDecoration)
            layoutManager = LinearLayoutManager(context)
            adapter = recyclerViewAdapter
        }
    }

    override fun initInjection(initialState: MovieListState?) {
        DaggerMovieListComponent.builder()
            .movieListModule(MovieListModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }

    companion object {
        private const val BUNDLE_KEY_MOVIE_LIST = "bundle_key_movie_list_items"

        fun newInstance(movieList: ContentList): MovieListFragment {
            return MovieListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BUNDLE_KEY_MOVIE_LIST, movieList)
                }
            }
        }
    }
}
