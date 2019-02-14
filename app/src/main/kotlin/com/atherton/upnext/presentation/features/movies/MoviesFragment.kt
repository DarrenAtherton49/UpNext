package com.atherton.upnext.presentation.features.movies


import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.atherton.upnext.R
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.getViewModel
import kotlinx.android.synthetic.main.fragment_movies.*
import javax.inject.Inject
import javax.inject.Named

class MoviesFragment : BaseFragment<MoviesAction, MoviesState, MoviesViewEffect, MoviesViewModel>() {

    override val layoutResId: Int = R.layout.fragment_movies
    override val stateBundleKey: String = "bundle_key_movies_state"

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(MoviesViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    private val activityViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: MoviesViewModel by lazy { getViewModel<MoviesViewModel>(vmFactory) }
    private val navController: NavController by lazy { findNavController() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMovieButton.setOnClickListener {
            viewModel.dispatch(MoviesAction.AddMovieButtonClicked)
        }
    }

    override fun renderState(state: MoviesState) {

    }

    override fun processViewEffects(viewEffect: MoviesViewEffect) {
        when (viewEffect) {
            is MoviesViewEffect.ShowSearchScreen -> navController.navigate(R.id.actionGoToSearch)
        }
    }

    override fun initInjection(initialState: MoviesState?) {
        DaggerMoviesComponent.builder()
            .moviesModule(MoviesModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }
}
