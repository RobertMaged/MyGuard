package com.rmsr.myguard.utils

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class RxJavaSchedulersRule(
    private val scheduler: Scheduler = Schedulers.trampoline()
) : TestWatcher() {


    override fun starting(description: Description) {
        RxJavaPlugins.setComputationSchedulerHandler { scheduler }
        RxJavaPlugins.setIoSchedulerHandler { scheduler }
        RxJavaPlugins.setNewThreadSchedulerHandler { scheduler }
        RxJavaPlugins.setSingleSchedulerHandler { scheduler }

        RxAndroidPlugins.setMainThreadSchedulerHandler { scheduler }
        RxAndroidPlugins.initMainThreadScheduler { scheduler }

    }

    override fun finished(description: Description) {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }
}