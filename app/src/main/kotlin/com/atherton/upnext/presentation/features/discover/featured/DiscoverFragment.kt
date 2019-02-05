package com.atherton.upnext.presentation.features.discover.featured

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.atherton.upnext.R
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.getViewModel
import kotlinx.android.synthetic.main.discover_search_field.*
import javax.inject.Inject
import javax.inject.Named

class DiscoverFragment : BaseFragment<DiscoverAction, DiscoverState, DiscoverViewModel>() {

    override val layoutResId: Int = R.layout.fragment_discover
    override val stateBundleKey: String = "bundle_key_discover_state"

    @field:[Inject Named(MainViewModelFactory.NAME)]
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @field:[Inject Named(DiscoverViewModelFactory.NAME)]
    lateinit var vmFactory: ViewModelProvider.Factory

    private val activityViewModel: MainViewModel by lazy {
        getActivityViewModel<MainViewModel>(mainVmFactory)

    }
    override val viewModel: DiscoverViewModel by lazy {
        getViewModel<DiscoverViewModel>(vmFactory)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //todo dispatch action
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

        searchEditText.setOnClickListener {
            //todo dispatch action to viewmodel to say 'search edit text clicked'
            //todo replace 'findNavController' with lazy delegate if used more than once
            findNavController().navigate(R.id.actionGoToSearchResults)
        }
    }

    override fun renderState(state: DiscoverState) {

    }

    //todo
    private fun initRecyclerView() {
//        recyclerAdapter = DailyAdapter { item ->
//            dailyViewModel.itemClicked(item)
//        }
//        recyclerView.apply {
//            adapter = recyclerAdapter
//            layoutManager = LinearLayoutManager(context)
//        }
    }

    override fun initInjection(initialState: DiscoverState?) {
        DaggerDiscoverComponent.builder()
            .discoverModule(DiscoverModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }

    companion object {
        fun newInstance() = DiscoverFragment()
    }
}
