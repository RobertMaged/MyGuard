package com.rmsr.myguard.presentation.workers

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.hilt.work.HiltWorkerFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.rmsr.myguard.RxJavaSchedulersRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest

import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@MediumTest
@HiltAndroidTest
class ScheduleWorkerTest {

    @get:Rule(order = 0)
    var hiltTestRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    var rxJavaSchedulersRule = RxJavaSchedulersRule()

    private lateinit var context: Context
    private lateinit var workManager: WorkManager

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Before
    fun setUp() {
        hiltTestRule.inject()
        context = ApplicationProvider.getApplicationContext()
//        context = InstrumentationRegistry.getInstrumentation().targetContext

        //integration
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            // .setWorkerFactory(workerFactory)
            .build()

        // WorkManager.initialize(context, config)

        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        workManager = WorkManager.getInstance(context)
    }

    @Test
    fun scheduleWorkReturnSuccess() {
        val worker =
            TestListenableWorkerBuilder<ScheduleWorker>(context).setWorkerFactory(workerFactory)
                .build()

        val resultData = workDataOf("new_leaks_titles" to emptyArray<String>())


        worker.createWork().test().assertNoErrors().assertValue(ListenableWorker.Result.retry())
    }

    @Test
    fun testPeriodicWithConstraints() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val request =
            PeriodicWorkRequestBuilder<ScheduleWorker>(1, TimeUnit.DAYS)
                //.setInitialDelay(repeatInterval, repeatIntervalTimeUnit)
                .setConstraints(constraints)
                .addTag("periodicLoop")
                .build()

        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)


        workManager.enqueue(request).result.get()

        testDriver?.setAllConstraintsMet(request.id)
        testDriver?.setInitialDelayMet(request.id)

        val workInfo = workManager.getWorkInfoById(request.id).get()
        val output = workInfo.outputData
    }
}