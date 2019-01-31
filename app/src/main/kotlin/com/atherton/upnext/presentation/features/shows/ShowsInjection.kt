package com.atherton.upnext.presentation.features.shows

import com.atherton.upnext.util.injection.AppComponent
import com.atherton.upnext.util.injection.PerView
import dagger.Component
import dagger.Module

@PerView
@Component(
    dependencies = [AppComponent::class],
    modules = [ShowsModule::class]
)
interface ShowsComponent {

    fun inject(showsFragment: ShowsFragment)
}


@Module
class ShowsModule
