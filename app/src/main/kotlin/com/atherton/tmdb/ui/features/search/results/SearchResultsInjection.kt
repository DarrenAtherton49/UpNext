package com.atherton.tmdb.ui.features.search.results

import com.atherton.tmdb.util.injection.AppComponent
import com.atherton.tmdb.util.injection.PerView
import dagger.Component
import dagger.Module

@PerView
@Component(
    dependencies = [AppComponent::class],
    modules = [SearchResultsModule::class]
)
interface SearchResultsComponent {

    fun inject(searchResultsFragment: SearchResultsFragment)
}


@Module
class SearchResultsModule
