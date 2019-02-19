package com.atherton.upnext.presentation.features.discover.featured

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.SearchModelViewMode
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
import kotlinx.android.synthetic.main.fragment_discover.*
import javax.inject.Inject
import javax.inject.Named


class DiscoverFragment : BaseFragment<DiscoverAction, DiscoverState, DiscoverViewEffect, DiscoverViewModel>() {

    override val layoutResId: Int = R.layout.fragment_discover
    override val stateBundleKey: String = "bundle_key_discover_state"

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(DiscoverViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    override val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: DiscoverViewModel by lazy { getViewModel<DiscoverViewModel>(vmFactory) }

    override val toolbarOptions: ToolbarOptions? = ToolbarOptions(
        toolbarResId = R.id.toolbar,
        titleResId = R.string.fragment_label_discover,
        menuResId = R.menu.menu_discover
    )

    private val viewPagerAdapter: FragmentViewPagerAdapter by lazy { FragmentViewPagerAdapter(childFragmentManager) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.dispatch(DiscoverAction.LoadViewMode)

        initViewPager()
    }

    override fun onMenuItemClicked(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_search -> {
                sharedViewModel.dispatch(MainAction.SearchActionClicked)
                true
            }
            R.id.action_toggle_view -> {
                viewModel.dispatch(DiscoverAction.ViewModeToggleActionClicked)
                true
            }
            else -> false
        }
    }

    override fun renderState(state: DiscoverState) {
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

    override fun processViewEffects(viewEffect: DiscoverViewEffect) {
        when (viewEffect) {
            is DiscoverViewEffect.ToggleViewMode -> {
                editMenuItem(R.id.action_toggle_view) {
                    isVisible = true
                    icon = when (viewEffect.viewMode) {
                        is SearchModelViewMode.List -> context?.getDrawableCompat(R.drawable.ic_view_grid_white_24dp)
                        is SearchModelViewMode.Grid -> context?.getDrawableCompat(R.drawable.ic_view_list_white_24dp)
                    }
                }
                sharedViewModel.dispatch(MainAction.ViewModeToggleChanged)
            }
        }
    }

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {}

    private fun initViewPager() {
        viewPagerAdapter.clear()
        val id = Math.random().toLong()
        val id2 = Math.random().toLong()
        viewPagerAdapter.addFragment(id, "hello $id", DiscoverTabFragment.newInstance(id.toString()))
        viewPagerAdapter.addFragment(id2, "hello $id2", DiscoverTabFragment.newInstance(id2.toString()))
        viewPager.adapter = viewPagerAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun initInjection(initialState: DiscoverState?) {
        DaggerDiscoverComponent.builder()
            .discoverModule(DiscoverModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }
}
