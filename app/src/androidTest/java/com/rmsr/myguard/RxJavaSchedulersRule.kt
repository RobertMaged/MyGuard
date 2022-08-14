package com.rmsr.myguard

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class RxJavaSchedulersRule(
    private val scheduler: Scheduler = Schedulers.trampoline()
) : TestWatcher() {
    override fun starting(description: Description?) {
        RxJavaPlugins.setInitComputationSchedulerHandler { scheduler }
        RxJavaPlugins.setInitIoSchedulerHandler { scheduler }
        RxJavaPlugins.setInitSingleSchedulerHandler { scheduler }
        RxJavaPlugins.setInitNewThreadSchedulerHandler { scheduler }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
    }

    override fun finished(description: Description?) {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
    }
}