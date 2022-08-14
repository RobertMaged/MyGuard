package com.rmsr.myguard.presentation.ui.schedules

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeRight
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth.assertThat
import com.rmsr.myguard.R
import com.rmsr.myguard.domain.entity.Schedule
import com.rmsr.myguard.domain.entity.SearchQuery
import com.rmsr.myguard.launchFragmentInHiltContainer
import com.rmsr.myguard.presentation.ui.FragmentsFactoryForTest
import com.rmsr.myguard.presentation.ui.schedules.mainfragment.SchedulesMainFragment
import com.rmsr.myguard.presentation.ui.schedules.mainfragment.SchedulesViewModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@ExperimentalMaterialApi
@MediumTest
@HiltAndroidTest
class SchedulesMainFragmentTest {

    @get:Rule(order = 0)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 1)
    var hiltAndroidRule = HiltAndroidRule(this)

    @Inject
    lateinit var fragmentFactory: FragmentsFactoryForTest

    lateinit var context: Context

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
        context = ApplicationProvider.getApplicationContext()
    }


    @Test
    fun clickAddScheduleFabButton_navigateToAddScheduleFragment() {
        val navController2: NavController = mockk()
        val navController = TestNavHostController(context)

        launchFragmentInHiltContainer<SchedulesMainFragment>(
            fragmentFactory = fragmentFactory
        ) {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.scheduleFragment)
            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.add_fab)).check(matches(ViewMatchers.isCompletelyDisplayed()))
        onView(withId(R.id.add_fab)).perform(ViewActions.click())

        assertThat(navController.currentDestination?.id)
            .isEqualTo(R.id.addScheduleCheckFragment)
//        verify {
//            navController2.navigate(R.id.action_scheduleCheckFragment_to_addScheduleCheckFragment)
//        }
    }

    @Test
    fun clickScheduleCheckSettingFab_navigateToScheduleCheckTimeFragment() {
//        val navController = mock(NavController::class.java)
        val navController = TestNavHostController(context)


        launchFragmentInHiltContainer<SchedulesMainFragment>(
            fragmentFactory = fragmentFactory
        ) {
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.scheduleFragment)

            Navigation.setViewNavController(requireView(), navController)
        }

        onView(withId(R.id.schedule_setting_fab))
            .check(ViewAssertions.matches(ViewMatchers.isCompletelyDisplayed()))
            .perform(ViewActions.actionWithAssertions(ViewActions.click()))

        assertThat(navController.currentDestination?.id).isEqualTo(R.id.schedulesCheckIntervalDialog)
//        verify(navController).navigate(ScheduleCheckFragmentDirections.actionScheduleCheckFragmentToCheckTimeDiagFragment())
    }

    @Test
    fun checkRecyclerview_fakeDataLoadedToIt() {

        var adapter = _ScheduleAdapter()
        var recycler: RecyclerView? = null
        var viewModel: SchedulesViewModel? = null

        launchFragmentInHiltContainer<SchedulesMainFragment>(
            fragmentFactory = fragmentFactory
        ) {
//            adapter = scheduleCheckAdapter
            recycler = requireView().findViewById(R.id.schedule_check_recycler_view)
            (recycler as RecyclerView).adapter = adapter
            adapter.setList(
                List(3) {
                    val breach = SearchQuery.DomainName(query = "Test ${it + 1}")
                    Schedule(searchQuery = breach)
                }
            )
        }


        assertThat(adapter.itemCount).isEqualTo(3)
        assertThat(recycler?.adapter?.itemCount).isEqualTo(3)

        onView(withId(R.id.schedule_check_recycler_view))
            .check(matches(isCompletelyDisplayed()))
            .check(matches(hasChildCount(3)))

            .perform(
                ViewActions.actionWithAssertions(
                    RecyclerViewActions.actionOnItemAtPosition<_ScheduleAdapter.ScheduleHolder>(
                        0,
                        swipeLeft()
                    )
                )
            )

//        assertThat(adapter.itemCount).isEqualTo(2)
//        assertThat(recycler?.adapter?.itemCount).isEqualTo(2)

        onView(withId(R.id.schedule_check_recycler_view))
//            .check(matches(hasChildCount(2)))

            .perform(
                ViewActions.actionWithAssertions(
                    RecyclerViewActions.actionOnItemAtPosition<_ScheduleAdapter.ScheduleHolder>(
                        1,
                        swipeRight()
                    )
                )
            )

//        assertThat(adapter.itemCount).isEqualTo(1)
//        assertThat(recycler?.adapter?.itemCount).isEqualTo(1)

        //            .perform(
//                RecyclerViewActions.actionOnItemAtPosition<ScheduleAdapter.ScheduleHolder>(5, click())
//            )
//            .perform(
//                RecyclerViewActions.scrollToPosition<ScheduleAdapter.ScheduleHolder>(5)
//            )
//            .perform(
//                RecyclerViewActions.scrollTo<ScheduleAdapter.ScheduleHolder>(
//                        hasDescendant(withText("Test 1"))
//                )
//            )

    }
}