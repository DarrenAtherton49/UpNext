package com.atherton.upnext.presentation.features.settings

import android.view.MenuItem
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.R
import com.atherton.upnext.presentation.main.MainViewEffect
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.presentation.main.MainViewModelFactory
import com.atherton.upnext.util.base.BaseFragment
import com.atherton.upnext.util.base.ToolbarOptions
import com.atherton.upnext.util.extensions.getActivityViewModel
import com.atherton.upnext.util.extensions.getAppComponent
import com.atherton.upnext.util.extensions.getViewModel
import javax.inject.Inject
import javax.inject.Named

class SettingsFragment : BaseFragment<SettingsAction, SettingsState, SettingsViewEffect, SettingsViewModel>() {

    override val layoutResId: Int = R.layout.fragment_settings
    override val stateBundleKey: String = "bundle_key_settings_state"

    @Inject
    @field:Named(MainViewModelFactory.NAME)
    lateinit var mainVmFactory: ViewModelProvider.Factory

    @Inject
    @field:Named(SettingsViewModelFactory.NAME)
    lateinit var vmFactory: ViewModelProvider.Factory

    override val sharedViewModel: MainViewModel by lazy { getActivityViewModel<MainViewModel>(mainVmFactory) }
    override val viewModel: SettingsViewModel by lazy {
        getViewModel<SettingsViewModel>(vmFactory)
    }

    override val toolbarOptions: ToolbarOptions? = ToolbarOptions(
        toolbarResId = R.id.toolbar,
        titleResId = R.string.fragment_label_settings,
        menuResId = R.menu.menu_settings
    )

    override fun onMenuItemClicked(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            R.id.action_settings -> {
                //todo dispatch action to open settings
                true
            }
            else -> false
        }
    }

    override fun renderState(state: SettingsState) {

    }

    override fun processViewEffects(viewEffect: SettingsViewEffect) {}

    override fun processSharedViewEffects(viewEffect: MainViewEffect) {}

    override fun initInjection(initialState: SettingsState?) {
        DaggerSettingsComponent.builder()
            .settingsModule(SettingsModule(initialState))
            .mainModule(mainModule)
            .appComponent(getAppComponent())
            .build()
            .inject(this)
    }
}
