package com.atherton.upnext.presentation.features.movies.detail


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.presentation.main.MainAction
import com.atherton.upnext.presentation.main.MainViewEffect
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.base.ToolbarOptions
import com.atherton.upnext.util.extensions.*
import com.atherton.upnext.util.glide.GlideApp
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
                renderContent(state)
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

    private fun renderContent(state: MovieDetailState.Content) {
        val movie = state.movie

        GlideApp.with(this)
            .load(movie.backdropPath)
            .error(R.drawable.ic_broken_image_white_24dp)
            .into(backdropImageView)
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
