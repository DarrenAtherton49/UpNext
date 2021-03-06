package com.atherton.upnext.presentation.features.discover

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.DiscoverFilter
import com.atherton.upnext.domain.model.GridViewMode
import com.atherton.upnext.presentation.features.discover.content.DiscoverContentFragment
import com.atherton.upnext.presentation.main.MainAction
import com.atherton.upnext.presentation.main.MainViewEffect
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.presentation.base.BaseFragment
import com.atherton.upnext.presentation.util.toolbar.ToolbarOptions
import com.atherton.upnext.util.extension.*
import com.atherton.upnext.presentation.util.viewpager.FragmentViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_discover_tabs.*
import javax.inject.Inject
import javax.inject.Named


class DiscoverTabsFragment
    : BaseFragment<DiscoverTabsAction, DiscoverTabsState, DiscoverTabsViewEffect, DiscoverTabsViewModel>() {

    override val layoutResId: Int = R.layout.fragment_discover_tabs
    override val stateBundleKey: String = "bundle_key_discover_tabs_state"

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(DiscoverTabsViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    override val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: DiscoverTabsViewModel by lazy { getViewModel<DiscoverTabsViewModel>(vmFactory) }

    override val toolbarOptions: ToolbarOptions? = ToolbarOptions(
        toolbarResId = R.id.toolbar,
        titleResId = R.string.fragment_label_discover,
        menuResId = R.menu.menu_discover_tabs
    )

    private val viewPagerAdapter: FragmentViewPagerAdapter by lazy { FragmentViewPagerAdapter(childFragmentManager) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.dispatch(DiscoverTabsAction.LoadViewMode)

        if (savedInstanceState == null) {
            viewModel.dispatch(DiscoverTabsAction.Load)
        }

        initViewPager()
    }

    override fun onMenuItemClicked(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_search -> {
                sharedViewModel.dispatch(MainAction.SearchActionClicked)
                true
            }
            R.id.action_toggle_view -> {
                viewModel.dispatch(DiscoverTabsAction.ViewModeToggleActionClicked)
                true
            }
            R.id.action_settings -> {
                viewModel.dispatch(DiscoverTabsAction.SettingsActionClicked)
                true
            }
            else -> false
        }
    }

    //todo uncomment loading/error states?
    override fun renderState(state: DiscoverTabsState) {
        when (state) {
            is DiscoverTabsState.Loading -> {
                tabLayout.isVisible = false
                viewPager.isVisible = false
            }
            is DiscoverTabsState.Content -> {
                if (state.results.isEmpty()) {
                    tabLayout.isVisible = false
                    viewPager.isVisible = false
                } else {
                    tabLayout.isVisible = true
                    viewPager.isVisible = true
                    populateFilterTabs(state.results)
                }
            }
            is DiscoverTabsState.Error -> {
                // progressBar.isVisible = false
                // tabLayout.isVisible = false
                // viewPager.isVisible = false
                // errorLayout.isVisible = true
                // errorTextView.text = state.failure.generateErrorMessage(requireContext())
            }
        }
    }

    override fun processViewEffects(viewEffect: DiscoverTabsViewEffect) {
        when (viewEffect) {
            is DiscoverTabsViewEffect.ViewModeToggled -> {
                updateViewModeIcon(viewEffect.viewMode)
                sharedViewModel.dispatch(MainAction.ViewModeToggled(viewEffect.viewMode))
            }
            is DiscoverTabsViewEffect.ViewModeLoaded -> updateViewModeIcon(viewEffect.viewMode)
            is DiscoverTabsViewEffect.ShowSettingsScreen -> {
                sharedViewModel.dispatch(MainAction.SettingsActionClicked)
            }
        }
    }

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {}

    private fun updateViewModeIcon(viewMode: GridViewMode) {
        editMenuItem(R.id.action_toggle_view) {
            isVisible = true
            icon = when (viewMode) {
                is GridViewMode.List -> context?.getDrawableCompat(R.drawable.ic_view_grid_white_24dp)
                is GridViewMode.Grid -> context?.getDrawableCompat(R.drawable.ic_view_list_white_24dp)
            }
        }
    }

    private fun initViewPager() {
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun populateFilterTabs(filters: List<DiscoverFilter>) {
        viewPagerAdapter.clear()
        filters.forEach { filter ->
            val filterName = when (filter) {
                is DiscoverFilter.Preset -> getString(filter.nameResId)
                is DiscoverFilter.Custom -> filter.name
            }
            viewPagerAdapter.addFragment(filter.id, filterName, DiscoverContentFragment.newInstance(filter))
        }
        viewPagerAdapter.notifyDataSetChanged()
    }

    override fun initInjection(initialState: DiscoverTabsState?) {
        DaggerDiscoverTabsComponent.builder()
            .discoverTabsModule(DiscoverTabsModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }
}
