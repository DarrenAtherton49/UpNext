package com.atherton.tmdb.ui.features.discover

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.atherton.tmdb.R
import com.atherton.tmdb.data.api.TmdbSearchService
import com.atherton.tmdb.ui.main.MainViewModel
import com.atherton.tmdb.util.base.BaseFragment
import com.atherton.tmdb.util.extensions.getActivityViewModel
import com.atherton.tmdb.util.extensions.getAppComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.discover_search_field.*
import javax.inject.Inject

class DiscoverFragment : BaseFragment() {

    override val layoutResId: Int = R.layout.fragment_discover

    @Inject lateinit var vmFactory: ViewModelProvider.Factory
    private lateinit var discoverViewModel: DiscoverViewModel
    private lateinit var mainViewModel: MainViewModel
    //private lateinit var recyclerAdapter: ScheduleAdapter //todo

    @Inject lateinit var api: TmdbSearchService

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //scheduleViewModel = getViewModel(vmFactory, DailyListViewModel::class.java) //todo
        mainViewModel = getActivityViewModel(vmFactory, MainViewModel::class.java)

        observeViewModels()

        val x = api.searchMulti("Gleaming the cube")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                it.results.forEach { res ->
                    Log.d("darren", res.toString())
                }
            }, {
                Log.d("darren", it.toString())
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()

        searchEditText.setOnClickListener {
            //todo dispatch action to viewmodel to say 'search edit text clicked'
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
