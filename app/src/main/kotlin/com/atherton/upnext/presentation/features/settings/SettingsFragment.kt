package com.atherton.upnext.presentation.features.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.atherton.upnext.R
import com.atherton.upnext.presentation.main.MainActivity
import com.atherton.upnext.presentation.main.MainModule
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.glide.GlideApp
import kotlinx.android.synthetic.main.base_app_bar.*
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject
import javax.inject.Named

class SettingsFragment : Fragment() {

    @Inject
    @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }

    private val mainActivity: MainActivity by lazy { activity as MainActivity }

    private val recyclerViewAdapter: SettingsAdapter by lazy {
        SettingsAdapter(GlideApp.with(this)) { setting ->
            onSettingClicked(setting)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initInjection()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
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
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = recyclerViewAdapter
        recyclerViewAdapter.submitList(populateSettings())
    }

    private fun populateSettings(): List<Setting> {
        return listOf(
            Setting(getString(R.string.settings_open_source_licenses), R.drawable.ic_description_white_24dp)
        )
    }

    private fun onSettingClicked(setting: Setting) {

    }

    private fun initInjection() {
        DaggerSettingsComponent.builder()
            .mainModule(MainModule(null))
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }
}

data class Setting(val title: String, val logoResId: Int)

