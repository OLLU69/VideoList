package ollu.dp.ua.videolist.core.di

import dagger.Component
import ollu.dp.ua.videolist.AndroidApplication
import ollu.dp.ua.videolist.core.di.viewmodel.ViewModelModule
import ollu.dp.ua.videolist.features.movies.MovieDetailsFragment
import ollu.dp.ua.videolist.features.movies.MoviesFragment
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class])
interface ApplicationComponent {
    fun inject(application: AndroidApplication)

    fun inject(moviesFragment: MoviesFragment)
    fun inject(movieDetailsFragment: MovieDetailsFragment)
}
