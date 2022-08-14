package com.rmsr.myguard.presentation.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.rmsr.myguard.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.action.ViewActions as myActions

@OptIn(ExperimentalMaterialApi::class)
@HiltAndroidTest
class HomeFragmentTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val fragmentScenario = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun isEmailFragmentIsVisible() {
        onView(withId(R.id.home_motion_layout)).check(matches(isDisplayed()))
        onView(withId(R.id.home_search_input_layout)).perform(typeText("Adobe"))
//        onView(withId(R.id.info_home_search)).perform(click())
        Thread.sleep(1000)
//        onView(withId(R.id.result_recycler_view)).perform(RecyclerViewActions
//                .actionOnItemAtPosition<EmailResultAdapter.ResultHolder>(0, click()))

        //onView(withId(R.id.breachDetailFragment)).perform(myActions.pressBack())

        myActions.pressBackUnconditionally()
    }


}