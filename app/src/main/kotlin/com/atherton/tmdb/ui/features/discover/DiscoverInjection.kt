package com.atherton.tmdb.ui.features.discover

import com.atherton.tmdb.ui.features.discover.featured.DiscoverFragment
import com.atherton.tmdb.ui.features.discover.search.SearchResultsFragment
import com.atherton.tmdb.util.injection.AppComponent
import com.atherton.tmdb.util.injection.PerView
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
