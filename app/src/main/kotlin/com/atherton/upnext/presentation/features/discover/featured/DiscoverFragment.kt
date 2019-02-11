package com.atherton.upnext.presentation.features.discover.featured

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.domain.model.Response
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.*
import com.atherton.upnext.util.glide.GlideApp
import com.atherton.upnext.util.recyclerview.LinearSpacingItemDecoration
import kotlinx.android.synthetic.main.base_recycler_view.*
import kotlinx.android.synthetic.main.discover_error_layout.*
import kotlinx.android.synthetic.main.discover_search_field.*
import kotlinx.android.synthetic.main.fragment_discover.*
import javax.inject.Inject
import javax.inject.Named


class DiscoverFragment : BaseFragment<DiscoverAction, DiscoverState, DiscoverViewModel>() {

    override val layoutResId: Int = com.atherton.upnext.R.layout.fragment_discover
    override val stateBundleKey: String = "bundle_key_discover_state"

    @Inject @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject @field:Named(DiscoverViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    private val activityViewModel: MainViewModel by lazy {
        getActivityViewModel<MainViewModel>(mainVmFactory)
    }
    override val viewModel: DiscoverViewModel by lazy {
        getViewModel<DiscoverViewModel>(vmFactory)
    }
    private val recyclerViewAdapter: DiscoverSectionAdapter by lazy {
        DiscoverSectionAdapter(
            GlideApp.with(this),
            resources.getDimensionPixelSize(R.dimen.discover_result_item_recyclerview_spacing)
        ) { searchModel ->
            //todo viewModel.dispatch(DiscoverAction.SearchModelClicked(searchModel))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

        viewModel.dispatch(DiscoverAction.Load)

        searchEditText.setOnClickListener {
            //todo dispatch action to viewmodel to say 'search edit query clicked'
            //todo replace 'findNavController' with lazy delegate if used more than once
            findNavController().navigate(R.id.actionGoToSearchResults)
        }

        retryButton.setOnClickListener {
            viewModel.dispatch(DiscoverAction.RetryButtonClicked)
        }

        initRecyclerView()
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

    private fun initRecyclerView() {
        val itemSpacing = resources.getDimensionPixelSize(R.dimen.discover_section_recyclerview_spacing)
        recyclerView.apply {
            setHasFixedSize(true)
            addItemDecoration(LinearSpacingItemDecoration(itemSpacing, LinearSpacingItemDecoration.Orientation.Vertical))
            adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(context)
        }
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
            is DiscoverTitle.Popular -> getString(com.atherton.upnext.R.string.discover_section_title_popular)
            is DiscoverTitle.NowPlaying -> getString(com.atherton.upnext.R.string.discover_section_title_now_playing)
            is DiscoverTitle.TopRated -> getString(com.atherton.upnext.R.string.discover_section_title_top_rated)
        }
    }
}
