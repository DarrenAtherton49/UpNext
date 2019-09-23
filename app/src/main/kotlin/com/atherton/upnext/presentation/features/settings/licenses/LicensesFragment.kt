package com.atherton.upnext.presentation.features.settings.licenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.presentation.main.*
import com.atherton.upnext.util.extension.getActivityViewModel
import com.atherton.upnext.util.extension.getAppComponent
import kotlinx.android.synthetic.main.base_app_bar.*
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject
import javax.inject.Named

class LicensesFragment : Fragment() {

    @Inject
    @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }

    private val mainActivity: MainActivity by lazy { activity as MainActivity }

    private val recyclerViewAdapter: LicensesAdapter by lazy {
        LicensesAdapter { license ->
            onViewLicenseClicked(license)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initInjection()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_licenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        initRecyclerView()
    }

    private fun setupToolbar() {
        toolbar.setupWithNavController(mainActivity.navController, mainActivity.appBarConfiguration)
        toolbar.title = getString(R.string.fragment_label_licenses)
    }

    private fun initRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        recyclerView.adapter = recyclerViewAdapter
        recyclerViewAdapter.submitList(generateLicenses(requireContext()))
    }

    private fun onViewLicenseClicked(license: License) {
        sharedViewModel.dispatch(MainAction.LicenseClicked(license))
    }

    private fun initInjection() {
        DaggerLicensesComponent.builder()
            .mainModule(MainModule(null))
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }
}

data class License(val name: String, val contributor: String, val description: String, val url: String)
