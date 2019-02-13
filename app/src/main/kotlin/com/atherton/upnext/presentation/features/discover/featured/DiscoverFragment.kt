package com.atherton.upnext.presentation.features.discover.featured

import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.DiscoverViewMode
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.*
import com.atherton.upnext.util.glide.GlideApp
import com.atherton.upnext.util.recyclerview.LinearSpacingItemDecoration
import kotlinx.android.synthetic.main.base_recycler_view.*
import kotlinx.android.synthetic.main.discover_error_layout.*
import kotlinx.android.synthetic.main.fragment_discover.*
import javax.inject.Inject
import javax.inject.Named


class DiscoverFragment : BaseFragment<DiscoverAction, DiscoverState, DiscoverViewEffect, DiscoverViewModel>() {

    override val layoutResId: Int = com.atherton.upnext.R.layout.fragment_discover
    override val stateBundleKey: String = "bundle_key_discover_state"

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(DiscoverViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    private val activityViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: DiscoverViewModel by lazy { getViewModel<DiscoverViewModel>(vmFactory) }
    private val navController: NavController by lazy { findNavController() }

    private val recyclerViewAdapter: DiscoverCarouselAdapter by lazy {
        DiscoverCarouselAdapter(
            GlideApp.with(this),
            resources.getDimensionPixelSize(R.dimen.discover_carousel_recyclerview_child_spacing)
        ) { searchModel ->
            viewModel.dispatch(DiscoverAction.SearchModelClicked(searchModel))
        }
    }

    private var viewMode: DiscoverViewMode? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCarouselRecyclerView() //todo remove?

        viewModel.dispatch(DiscoverAction.Load)

        retryButton.setOnClickListener {
            viewModel.dispatch(DiscoverAction.RetryButtonClicked)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        if (menu != null && inflater != null) {
            inflater.inflate(R.menu.menu_discover, menu)
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        viewMode?.let {
            val viewToggleLogo = when (it) {
                is DiscoverViewMode.Carousel -> context?.getDrawableCompat(R.drawable.ic_view_grid_white_24dp)
                is DiscoverViewMode.Grid -> context?.getDrawableCompat(R.drawable.ic_view_list_white_24dp)
            }
            val menuItem = menu?.findItem(R.id.action_toggle_view)
            menuItem?.isVisible = true
            menuItem?.icon = viewToggleLogo
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                viewModel.dispatch(DiscoverAction.SearchActionClicked)
                true
            }
            R.id.action_toggle_view -> {
                viewModel.dispatch(DiscoverAction.ViewModeToggleActionClicked)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun renderState(state: DiscoverState) {
        when (state) {
            is DiscoverState.Loading -> {
                progressBar.isVisible = true
                recyclerView.isVisible = false
                errorLayout.isVisible = false
            }
            is DiscoverState.Content -> {
                progressBar.isVisible = false
                if (state.results.isEmpty()) {
                    recyclerView.isVisible = false
                    errorLayout.isVisible = true
                    errorTextView.text = getString(R.string.search_results_error_network_try_again)
                } else {
                    recyclerView.isVisible = true
                    errorLayout.isVisible = false
                    recyclerViewAdapter.submitData(state.results)
                }
            }
            is DiscoverState.Error -> {
                progressBar.isVisible = false
                recyclerView.isVisible = false
                errorLayout.isVisible = true
                errorTextView.text = state.failure.generateErrorMessage(requireContext())
                retryButton.isVisible = state.failure is Response.Failure.NetworkError
            }
        }
    }

    override fun processViewEffects(viewEffect: DiscoverViewEffect) {
        when (viewEffect) {
            is DiscoverViewEffect.ToggleViewMode -> {
                viewMode = viewEffect.viewMode
                activity?.invalidateOptionsMenu()
            }
            is DiscoverViewEffect.ShowSearchModelDetailScreen -> {
                //todo
            }
            is DiscoverViewEffect.ShowSearchResultsScreen -> navController.navigate(R.id.actionGoToSearchResults)
        }
    }

    private fun initCarouselRecyclerView() {
        //todo if (!notInitialized) - take into account rotation
        val itemSpacing = resources.getDimensionPixelSize(R.dimen.discover_carousel_recyclerview_section_spacing)
        recyclerView.apply {
            setHasFixedSize(true)
            addItemDecoration(LinearSpacingItemDecoration(itemSpacing, LinearSpacingItemDecoration.Orientation.Vertical))
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun initGridViewPager() {
        //todo if (!notInitialized) - take into account rotation
//        viewPagerAdapter.addFragment(getString(R.string.shows_tab_up_next), )
//        viewPagerAdapter.addFragment(getString(R.string.shows_tab_watchlist), )
//        viewPagerAdapter.addFragment(getString(R.string.shows_tab_finished), )
//        viewPager.adapter = viewPagerAdapter
//        tabLayout.setupWithViewPager(viewPager)
    }

    override fun initInjection(initialState: DiscoverState?) {
        DaggerDiscoverComponent.builder()
            .discoverModule(DiscoverModule(initialState, this::titleProvider))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }

    private fun titleProvider(discoverTitle: DiscoverTitle): String {
        return when (discoverTitle) {
            is DiscoverTitle.Popular -> getString(com.atherton.upnext.R.string.discover_carousel_section_title_popular)
            is DiscoverTitle.NowPlaying -> getString(com.atherton.upnext.R.string.discover_carousel_section_title_now_playing)
            is DiscoverTitle.TopRated -> getString(com.atherton.upnext.R.string.discover_carousel_section_title_top_rated)
        }
    }
}
