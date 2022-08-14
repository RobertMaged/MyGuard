package com.rmsr.myguard.presentation.ui

import androidx.compose.material.ExperimentalMaterialApi
import com.rmsr.myguard.data.database.BreachDaoTest
import com.rmsr.myguard.data.database.entity.new_version.HistoryBreachDaoTest
import com.rmsr.myguard.presentation.ui.schedules.SchedulesMainFragmentTest
import com.rmsr.myguard.presentation.workers.ScheduleWorkerTest
import org.junit.runner.RunWith
import org.junit.runners.Suite

@OptIn(ExperimentalMaterialApi::class)
@RunWith(Suite::class)
@Suite.SuiteClasses(
    MainActivityTest::class,
    HomeFragmentTest::class,
    BreachDaoTest::class,
    HistoryBreachDaoTest::class,
    SchedulesMainFragmentTest::class,
    ScheduleWorkerTest::class
    //SearchCachingFacadeAndroidTest::class
)
class AppTestSuite {
}