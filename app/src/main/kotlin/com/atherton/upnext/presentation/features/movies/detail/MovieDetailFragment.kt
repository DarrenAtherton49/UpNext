package com.atherton.upnext.presentation.features.movies.detail


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Movie
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.presentation.main.MainAction
import com.atherton.upnext.presentation.main.MainViewEffect
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.base.ToolbarOptions
import com.atherton.upnext.util.extensions.*
import com.atherton.upnext.util.glide.GlideApp
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.detail_screen_appbar.*
import kotlinx.android.synthetic.main.error_retry_layout.*
import kotlinx.android.synthetic.main.fragment_movie_detail.*
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class MovieDetailFragment : BaseFragment<MovieDetailAction, MovieDetailState, MovieDetailViewEffect, MovieDetailViewModel>() {

    override val layoutResId: Int = R.layout.fragment_movie_detail
    override val stateBundleKey: String = "bundle_key_movie_detail_state"

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(MovieDetailViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    override val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: MovieDetailViewModel by lazy { getViewModel<MovieDetailViewModel>(vmFactory) }

    override val toolbarOptions: ToolbarOptions? = ToolbarOptions(
        toolbarResId = R.id.toolbar,
        titleResId = null,
        menuResId = R.menu.menu_movie_detail
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val movieId = MovieDetailFragmentArgs.fromBundle(it).movieId
            //todo
//            retryButton.setOnClickListener {
//                viewModel.dispatch(MovieDetailAction.RetryButtonClicked(id))
//            }
            if (savedInstanceState == null) {
                viewModel.dispatch(MovieDetailAction.Load(movieId))
            }
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

    override fun renderState(state: MovieDetailState) {
        Timber.tag("darren").d(state.toString())
        when (state) {
            is MovieDetailState.Loading -> {
                //todo content.isVisible = false
                errorLayout.isVisible = false
                progressBar.isVisible = true
            }
            is MovieDetailState.Content -> {
                progressBar.isVisible = false
                errorLayout.isVisible = false
                //todo content.isVisible = true
                renderMovie(state.movie)
            }
            is MovieDetailState.Error -> {
                progressBar.isVisible = false
                //todo content.isVisible = false
                errorLayout.isVisible = true
                errorTextView.text = state.failure.generateErrorMessage(requireContext())
                retryButton.isVisible = state.failure is Response.Failure.NetworkError
            }
        }
    }

    private fun renderMovie(movie: Movie) {
        renderMovieImages(movie)
        titleTextView.text = movie.title
        overviewTextView.setTextOrHide(movie.overview)
        releaseDateTextView.setTextOrHide(movie.releaseDate)
        renderMovieRuntime(movie)
        //todo add age rating
        renderMovieGenres(movie)

        // vote average in circle

        //todo set button image based on whether show is already in watchlist or not
        addToWatchlistButton.show(true)
    }

    private fun renderMovieImages(movie: Movie) {
        GlideApp.with(this)
            .load(movie.backdropPath)
            .centerCrop()
            .error(R.drawable.ic_broken_image_white_24dp)
            .into(backdropImageView)

        val posterOptions = RequestOptions()
            .transforms(CenterCrop(), RoundedCorners(20))
            .error(R.drawable.ic_broken_image_white_24dp)

        GlideApp.with(this)
            .load(movie.posterPath)
            .apply(posterOptions)
            .into(posterImageView)
    }

    private fun renderMovieRuntime(movie: Movie) {
        if (movie.detail?.runtime != null) {
            val runtimeText = resources.getQuantityString(
                R.plurals.movie_tv_detail_minutes_plural,
                movie.detail.runtime,
                movie.detail.runtime
            )
            runtimeTextView.text = runtimeText
        } else {
            runtimeTextView.isVisible = false
        }
    }

    private fun renderMovieGenres(movie: Movie) {
        if (movie.detail?.genres != null) {
            movie.detail.genres.forEach { genre ->
                genre.name?.let { name ->
                    val chip = Chip(context).apply {
                        text = name
                        isClickable = false
                        isCheckable = false
                    }
                    genreChipGroup.addView(chip as View)
                }
            }
        } else {
            genreChipHorizontalScrollView.isVisible = false
        }
    }

    override fun processViewEffects(viewEffect: MovieDetailViewEffect) {}
    override fun processSharedViewEffects(viewEffect: MainViewEffect) {}

    override fun initInjection(initialState: MovieDetailState?) {
        DaggerMovieDetailComponent.builder()
            .movieDetailModule(MovieDetailModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }
}
