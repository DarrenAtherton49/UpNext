package com.atherton.upnext.presentation.features.discover

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.SearchModelViewMode
import com.atherton.upnext.presentation.features.discover.content.DiscoverContentFragment
import com.atherton.upnext.presentation.main.MainAction
import com.atherton.upnext.presentation.main.MainViewEffect
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.base.ToolbarOptions
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.getDrawableCompat
import com.atherton.upnext.util.extensions.getViewModel
import com.atherton.upnext.util.viewpager.FragmentViewPagerAdapter
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
            else -> false
        }
    }

    override fun renderState(state: DiscoverTabsState) {
        when (state) {
//            is DiscoverState.Loading -> {
//                progressBar.isVisible = true
//                recyclerView.isVisible = false
//                errorLayout.isVisible = false
//            }
//            is DiscoverState.Content -> {
//                progressBar.isVisible = false
//                if (state.results.isEmpty()) {
//                    recyclerView.isVisible = false
//                    errorLayout.isVisible = true
//                    errorTextView.text = getString(R.string.search_error_network_try_again)
//                } else {
//                    recyclerView.isVisible = true
//                    errorLayout.isVisible = false
//                    //todo render tabs
//                    recyclerViewAdapter.submitList(state.results)
//                }
//            }
//            is DiscoverState.Error -> {
//                progressBar.isVisible = false
//                recyclerView.isVisible = false
//                errorLayout.isVisible = true
//                errorTextView.text = state.failure.generateErrorMessage(requireContext())
//                retryButton.isVisible = state.failure is Response.Failure.NetworkError
//            }
        }
    }

    override fun processViewEffects(viewEffect: DiscoverTabsViewEffect) {
        when (viewEffect) {
            is DiscoverTabsViewEffect.ToggleViewMode -> {
                editMenuItem(R.id.action_toggle_view) {
                    isVisible = true
                    icon = when (viewEffect.viewMode) {
                        is SearchModelViewMode.List -> context?.getDrawableCompat(R.drawable.ic_view_grid_white_24dp)
                        is SearchModelViewMode.Grid -> context?.getDrawableCompat(R.drawable.ic_view_list_white_24dp)
                    }
                }
                sharedViewModel.dispatch(MainAction.ViewModeToggleChanged(viewEffect.viewMode))
            }
        }
    }

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {}

    private fun initViewPager() {
        viewPagerAdapter.addFragment(1L, "", DiscoverContentFragment.newInstance("hello"))
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    //todo state param with filters (tabs) in
    private fun populateViewPager() {
//        viewPagerAdapter.clear()
//        state.forEach { filter ->
//            //todo get title from string resources,
//            viewPagerAdapter.addFragment(filter.id, filter.title, DiscoverContentFragment.newInstance(filter))
//        }
//        viewPagerAdapter.notifyDataSetChanged()
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
