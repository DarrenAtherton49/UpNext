package com.atherton.upnext.util.injection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.presentation.features.discover.featured.DiscoverViewModel
import com.atherton.upnext.presentation.main.MainViewModel
import com.atherton.upnext.util.base.ViewModelFactory
import com.atherton.upnext.util.base.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    //================================================================================
    // Main
    //================================================================================

    @Binds @IntoMap @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    //================================================================================
    // Discover
    //================================================================================

    @Binds @IntoMap @ViewModelKey(DiscoverViewModel::class)
    abstract fun bindDiscoverViewModel(viewModel: DiscoverViewModel): ViewModel

    //================================================================================
    // Factory
    //================================================================================

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
