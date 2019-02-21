package com.atherton.upnext.presentation.features.movies.detail


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.presentation.features.MovieDetail.detail.*
import com.atherton.upnext.presentation.main.MainAction
import com.atherton.upnext.presentation.main.MainViewEffect
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.base.ToolbarOptions
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.getViewModel
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
        titleResId = R.string.fragment_label_movie_detail,
        menuResId = R.menu.menu_movie_detail
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

    override fun renderState(state: MovieDetailState) {}
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


    companion object {
        fun newInstance(): MovieDetailFragment = MovieDetailFragment()
    }
}
