package com.atherton.upnext.ui.features.discover

import com.atherton.upnext.ui.features.discover.featured.DiscoverFragment
import com.atherton.upnext.ui.features.discover.search.SearchResultsFragment
import com.atherton.upnext.util.injection.AppComponent
import com.atherton.upnext.util.injection.PerView
import dagger.Component
import dagger.Module

@PerView
@Component(
    dependencies = [AppComponent::class],
    modules = [DiscoverModule::class]
)
interface DiscoverComponent {

    fun inject(discoverFragment: DiscoverFragment)
    fun inject(searchResultsFragment: SearchResultsFragment)
}


@Module
class DiscoverModule
