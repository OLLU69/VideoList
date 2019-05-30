package ollu.dp.ua.videolist

import android.app.Application
import androidx.fragment.app.Fragment
import com.squareup.leakcanary.LeakCanary
import ollu.dp.ua.videolist.core.di.ApplicationComponent
import ollu.dp.ua.videolist.core.di.ApplicationModule
import ollu.dp.ua.videolist.core.di.DaggerApplicationComponent

class AndroidApplication : Application() {

    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerApplicationComponent
            .builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        this.injectMembers()
        this.initializeLeakDetection()
    }

    private fun injectMembers() = appComponent.inject(this)

    private fun initializeLeakDetection() {
        if (BuildConfig.DEBUG) LeakCanary.install(this)
    }
}

fun Fragment.appComponent(): ApplicationComponent? {
    return (this.activity?.application as? AndroidApplication)?.appComponent
}
