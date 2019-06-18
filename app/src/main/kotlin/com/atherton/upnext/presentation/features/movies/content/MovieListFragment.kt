package com.atherton.upnext.presentation.features.movies.content

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.MovieList
import com.atherton.upnext.presentation.main.MainAction
import com.atherton.upnext.presentation.main.MainViewEffect
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.base.ToolbarOptions
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.getViewModel
import com.atherton.upnext.util.extensions.isVisible
import com.atherton.upnext.util.glide.GlideApp
import kotlinx.android.synthetic.main.error_retry_layout.*
import kotlinx.android.synthetic.main.fragment_movie_list.*
import javax.inject.Inject
import javax.inject.Named

class MovieListFragment : BaseFragment<MovieListAction, MovieListState, MovieListViewEffect, MovieListViewModel>() {

    override val layoutResId: Int = R.layout.fragment_movie_list
    override val stateBundleKey: String by lazy { "bundle_key_movie_list_${movieList.id}_state" }
    private val movieList: MovieList by lazy { arguments?.getParcelable(BUNDLE_KEY_MOVIE_LIST) as MovieList }

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(MovieListViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    override val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: MovieListViewModel by lazy { getViewModel<MovieListViewModel>(vmFactory) }

    override val toolbarOptions: ToolbarOptions? = null

    private val recyclerViewAdapter: MovieListAdapter by lazy {
        MovieListAdapter(
            imageLoader = GlideApp.with(this),
            onClickListener = { movie -> viewModel.dispatch(MovieListAction.MovieClicked(movie)) }
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
        }
    }

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {}

    private fun initRecyclerView() {
        recyclerView.apply {
            setHasFixedSize(true)
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
        private const val BUNDLE_KEY_MOVIE_LIST = "movie_list_bundle_key_movie_list"

        fun newInstance(movieList: MovieList): MovieListFragment {
            return MovieListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BUNDLE_KEY_MOVIE_LIST, movieList)
                }
            }
        }
    }
}
