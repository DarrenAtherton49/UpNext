package com.atherton.tmdb.ui.main

import androidx.appcompat.app.AppCompatActivity
import com.atherton.tmdb.util.injection.AppComponent
import com.atherton.tmdb.util.injection.PerView
import dagger.Component
import dagger.Module
import dagger.Provides

@PerView
@Component(
        dependencies = [AppComponent::class],
        modules = [MainModule::class]
)
interface MainComponent {

    fun inject(mainActivity: MainActivity)
}


@Module
class MainModule(private val activity: AppCompatActivity) {

    @Provides
    @PerView
    internal fun provideMainViewPagerAdapter(): MainViewPagerAdapter {
        return MainViewPagerAdapter(activity.supportFragmentManager)
    }
}
