package com.atherton.upnext.presentation.features.discover.featured

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.atherton.upnext.R
import com.atherton.upnext.data.model.SearchModel
import com.atherton.upnext.data.repository.Response
import com.atherton.upnext.data.repository.search.SearchRepository
import com.atherton.upnext.presentation.features.discover.DaggerDiscoverComponent
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.getViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.discover_search_field.*
import javax.inject.Inject

class DiscoverFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_discover

    @Inject lateinit var vmFactory: ViewModelProvider.Factory
    private val activityViewModel: MainViewModel by lazy {
        getActivityViewModel(vmFactory, MainViewModel::class.java)

    }
    private val viewModel: DiscoverViewModel by lazy {
        getViewModel(vmFactory, DiscoverViewModel::class.java)
    }

    @Inject lateinit var repository: SearchRepository

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeViewModels()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

        searchEditText.setOnClickListener {
            //todo dispatch action to viewmodel to say 'search edit text clicked'
            //todo replace 'findNavController' with lazy delegate if used more than once
            findNavController().navigate(R.id.actionGoToSearchResults)
        }

        val x = repository.searchMulti("Gleaming the cube")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { response: Response<List<SearchModel>> ->
                when (response) {
                    is Response.Success -> {
                        Log.d("darren", response.toString())
                    }
                }
            }
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

    private fun observeViewModels() {

    }

    override fun initInjection() {
        DaggerDiscoverComponent.builder()
                .appComponent(getAppComponent())
                .build()
                .inject(this)
    }

    companion object {
        fun newInstance() = DiscoverFragment()
    }
}
