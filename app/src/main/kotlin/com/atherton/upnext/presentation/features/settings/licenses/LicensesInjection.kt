package com.atherton.upnext.presentation.features.settings.licenses

import com.atherton.upnext.presentation.main.MainComponent
import com.atherton.upnext.presentation.main.MainModule
import com.atherton.upnext.util.injection.AppComponent
import com.atherton.upnext.util.injection.PerView
import dagger.Component
import dagger.Module

@PerView
@Component(
    dependencies = [AppComponent::class],
    modules = [MainModule::class, LicensesModule::class]
)
interface LicensesComponent : MainComponent {

    fun inject(licensesFragment: LicensesFragment)
}


@Module
class LicensesModule
