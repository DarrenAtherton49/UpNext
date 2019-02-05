package com.atherton.upnext.presentation.features.movies


import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.getViewModel
import javax.inject.Inject
import javax.inject.Named

class MoviesFragment : BaseFragment<MoviesAction, MoviesState, MoviesViewModel>() {

    override val layoutResId: Int = R.layout.fragment_movies
    override val stateBundleKey: String = "bundle_key_movies_state"

    @field:[Inject Named(MainViewModelFactory.NAME)]
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @field:[Inject Named(MoviesViewModelFactory.NAME)]
    lateinit var vmFactory: ViewModelProvider.Factory

    private val activityViewModel: MainViewModel by lazy {
        getActivityViewModel<MainViewModel>(mainVmFactory)

    }
    override val viewModel: MoviesViewModel by lazy {
        getViewModel<MoviesViewModel>(vmFactory)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //todo dispatch action
    }

    override fun renderState(state: MoviesState) {

    }

    override fun initInjection(initialState: MoviesState?) {
        DaggerMoviesComponent.builder()
            .moviesModule(MoviesModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }

    companion object {
        fun newInstance() = MoviesFragment()
    }
}
