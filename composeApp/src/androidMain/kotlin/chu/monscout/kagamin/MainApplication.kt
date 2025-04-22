package chu.monscout.kagamin

import android.app.Application
import com.github.catomon.yukinotes.di.appModule
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(appModule)
        }
    }
}