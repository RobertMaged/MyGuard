package com.rmsr.myguard

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import leakcanary.LeakCanary
import javax.inject.Inject

@HiltAndroidApp
class MyGuardApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.VERBOSE)
            .setWorkerFactory(workerFactory)
            .build()
    }

//    override fun onCreate() {
//        super.onCreate()
//    if(BuildConfig.DEBUG)
//        Timber.plant(Timber.DebugTree())
//    else
//        Timber.plant(new CrashReportingTree())

//    }

    override fun onCreate() {
        super.onCreate()

        //Temporary Disable NightMode until implement in themes.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        LeakCanary.config = LeakCanary.config.copy(dumpHeap = false)
        LeakCanary.showLeakDisplayActivityLauncherIcon(false)
//        RxJavaPlugins.setErrorHandler { MyLog.e("Rob_BaseApplication", "Global RxHandler: $it") }

//        initNotificationChannels(applicationContext)

        //   ScheduleWorker.enqueuePeriodicWorkIfNotExist(WorkManager.getInstance(this))
    }

//    private fun initNotificationChannels(context: Context) {
//        val channels = buildList {
//            add(NotifiedWorker.getChannel())
//
//        }
//
//        NotificationManagerCompat.from(context).createNotificationChannelsCompat(channels)
//    }
}