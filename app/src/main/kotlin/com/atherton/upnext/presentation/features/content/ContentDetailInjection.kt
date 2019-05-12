package com.atherton.upnext.presentation.features.content

import androidx.lifecycle.ViewModelProvider
import com.atherton.upnext.domain.repository.ConfigRepository
import com.atherton.upnext.domain.usecase.GetMovieDetailUseCase
import com.atherton.upnext.domain.usecase.GetTvShowDetailUseCase
import com.atherton.upnext.presentation.main.MainComponent
import com.atherton.upnext.presentation.main.MainModule
import com.atherton.upnext.presentation.util.AppStringProvider
import com.atherton.upnext.util.injection.AppComponent
import com.atherton.upnext.util.injection.PerView
import com.atherton.upnext.util.threading.RxSchedulers
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Named

@PerView
@Component(
    dependencies = [AppComponent::class],
    modules = [MainModule::class, ContentDetailModule::class]
)
interface ContentDetailComponent : MainComponent {

    fun inject(contentDetailFragment: ContentDetailFragment)
}


@Module
class ContentDetailModule(private val initialState: ContentDetailState?) {

    @Provides
    @Named(ContentDetailViewModelFactory.NAME)
    @PerView internal fun provideViewModelFactory(
        getTvShowDetailUseCase: GetTvShowDetailUseCase,
        getMovieDetailUseCase: GetMovieDetailUseCase,
        configRepository: ConfigRepository,
        appStringProvider: AppStringProvider,
        schedulers: RxSchedulers
    ): ViewModelProvider.Factory {
        return ContentDetailViewModelFactory(
            initialState,
            getTvShowDetailUseCase,
            getMovieDetailUseCase,
            configRepository,
            appStringProvider,
            schedulers
        )
    }
}
