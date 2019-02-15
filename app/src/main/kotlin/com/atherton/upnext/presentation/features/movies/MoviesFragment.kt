package com.atherton.upnext.presentation.features.movies


import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.presentation.main.MainAction
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

    override val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: MoviesViewModel by lazy { getViewModel<MoviesViewModel>(vmFactory) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMovieButton.setOnClickListener {
            sharedViewModel.dispatch(MainAction.AddMovieButtonClicked)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (menu != null && inflater != null) {
            inflater.inflate(R.menu.menu_movies, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                sharedViewModel.dispatch(MainAction.SearchActionClicked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun renderState(state: MoviesState) {

    }

    override fun processViewEffects(viewEffect: MoviesViewEffect) {

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
