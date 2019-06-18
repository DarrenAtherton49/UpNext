package com.atherton.upnext.presentation.features.movies


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.MovieList
import com.atherton.upnext.presentation.features.movies.content.MovieListFragment
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
import com.atherton.upnext.util.viewpager.FragmentViewPagerAdapter
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

    override val toolbarOptions: ToolbarOptions? = ToolbarOptions(
        toolbarResId = R.id.toolbar,
        titleResId = R.string.fragment_label_movies,
        menuResId = R.menu.menu_movies
    )

    private val viewPagerAdapter: FragmentViewPagerAdapter by lazy { FragmentViewPagerAdapter(childFragmentManager) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMovieButton.setOnClickListener {
            sharedViewModel.dispatch(MainAction.AddMovieButtonClicked)
        }

        if (savedInstanceState == null) {
            viewModel.dispatch(MoviesAction.Load)
        }

        initViewPager()
    }

    override fun onMenuItemClicked(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_search -> {
                sharedViewModel.dispatch(MainAction.SearchActionClicked)
                true
            }
            R.id.action_settings -> {
                viewModel.dispatch(MoviesAction.SettingsActionClicked)
                true
            }
            else -> false
        }
    }

    //todo uncomment loading/error states?
    override fun renderState(state: MoviesState) {
        when (state) {
            is MoviesState.Loading -> {
                tabLayout.isVisible = false
                viewPager.isVisible = false
            }
            is MoviesState.Content -> {
                if (state.results.isEmpty()) {
                    tabLayout.isVisible = false
                    viewPager.isVisible = false
                } else {
                    tabLayout.isVisible = true
                    viewPager.isVisible = true
                    populateListTabs(state.results)
                }
            }
            is MoviesState.Error -> {
                // progressBar.isVisible = false
                // tabLayout.isVisible = false
                // viewPager.isVisible = false
                // errorLayout.isVisible = true
                // errorTextView.text = state.failure.generateErrorMessage(requireContext())
            }
        }
    }

    override fun processViewEffects(viewEffect: MoviesViewEffect) {
        when (viewEffect) {
           is MoviesViewEffect.ShowSettingsScreen -> {
               sharedViewModel.dispatch(MainAction.SettingsActionClicked)
           }
        }
    }

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {}

    private fun initViewPager() {
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun populateListTabs(movieLists: List<MovieList>) {
        viewPagerAdapter.clear()
        movieLists.forEach { movieList ->
            val filterName = when (movieList.name) {
                "Watchlist" -> getString(R.string.movies_tab_watchlist)
                "Watched" -> getString(R.string.movies_tab_watched)
                else -> movieList.name
            }
            viewPagerAdapter.addFragment(
                id = movieList.id,
                title = filterName,
                fragment = MovieListFragment.newInstance(movieList)
            )
        }
        viewPagerAdapter.notifyDataSetChanged()
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
