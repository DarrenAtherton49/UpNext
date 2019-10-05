package com.atherton.upnext.presentation.features.movies


import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.ContentList
import com.atherton.upnext.presentation.base.BaseFragment
import com.atherton.upnext.presentation.features.movies.content.MovieListFragment
import com.atherton.upnext.presentation.main.MainAction
import com.atherton.upnext.presentation.main.MainViewEffect
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.presentation.util.extension.onPageChanged
import com.atherton.upnext.presentation.util.toolbar.ToolbarOptions
import com.atherton.upnext.presentation.util.viewpager.FragmentViewPagerAdapter
import com.atherton.upnext.util.extension.getActivityViewModel
import com.atherton.upnext.util.extension.getAppComponent
import com.atherton.upnext.util.extension.getViewModel
import com.atherton.upnext.util.extension.isVisible
import kotlinx.android.synthetic.main.fragment_content_tabs.*
import javax.inject.Inject
import javax.inject.Named

class MoviesFragment : BaseFragment<MoviesAction, MoviesState, MoviesViewEffect, MoviesViewModel>() {

    override val layoutResId: Int = R.layout.fragment_content_tabs
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

    private var currentPage: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.dispatch(MoviesAction.Load)
        } else {
            val savedInstanceStateCurrentPage = savedInstanceState.getInt(BUNDLE_VIEWPAGER_CURRENT_PAGE)
            if (savedInstanceStateCurrentPage != 0) {
                currentPage = savedInstanceStateCurrentPage
            }
        }

        initViewPager()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        currentPage?.let {
            outState.putInt(BUNDLE_VIEWPAGER_CURRENT_PAGE, it)
        }
    }

    override fun onDestroyView() {
        viewPager.clearOnPageChangeListeners()
        super.onDestroyView()
    }

    override fun onMenuItemClicked(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_search -> {
                viewModel.dispatch(MoviesAction.SearchActionClicked)
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
            is MoviesViewEffect.ShowSearchScreen -> {
                sharedViewModel.dispatch(MainAction.SearchActionClicked)
            }
            is MoviesViewEffect.ShowSettingsScreen -> {
               sharedViewModel.dispatch(MainAction.SettingsActionClicked)
           }
        }
    }

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {}

    private fun initViewPager() {
        viewPager.clearOnPageChangeListeners()
        viewPager.onPageChanged { page ->
            currentPage = page
        }
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun populateListTabs(movieLists: List<ContentList>) {
        viewPagerAdapter.clear()
        movieLists.forEach { movieList ->
            val listName = when (movieList.name) {
                "Watchlist" -> getString(R.string.movies_tab_watchlist)
                "Watched" -> getString(R.string.movies_tab_watched)
                else -> movieList.name
            }
            viewPagerAdapter.addFragment(
                id = movieList.id,
                title = listName,
                fragment = MovieListFragment.newInstance(movieList)
            )
        }

        viewPagerAdapter.notifyDataSetChanged()

        scrollToPage(movieLists)
    }

    private fun scrollToPage(movieLists: List<ContentList>) {
        arguments?.let { args ->

            var currentItem: Int? = null

            // first we try to scroll to the initial list (page) if there is one (e.g. when user
            // has clicked 'see list' button).
            val initialListId: Long = MoviesFragmentArgs.fromBundle(args).initialListId
            if (initialListId != 0L) {
                val initialList: ContentList? = movieLists.find { list -> list.id == initialListId }
                initialList?.let {
                    args.remove("initialListId")
                    currentItem = it.id.toInt()
                }
            }

            // if the current page saved (e.g. on rotation) is not the same as the initial page,
            // then we scroll to the saved page.
            currentPage?.let {
                if (it != initialListId.toInt()) {
                    currentItem = it
                }
            }

            // finally, we scroll to the last known page.
            currentItem?.let {
                viewPager.currentItem = it
            }
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

    companion object {
        private const val BUNDLE_VIEWPAGER_CURRENT_PAGE = "bundle_movies_viewpager_current_page"
    }
}
