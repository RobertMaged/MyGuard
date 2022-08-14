package com.rmsr.myguard.presentation.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.MediumTest
import com.rmsr.myguard.R
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterialApi::class)
@MediumTest
@HiltAndroidTest
class MainActivityTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    fun isActivityInView() {
        //val activityScenario = ActivityScenario.launch(MainActivity::class.java)
//        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()))
    }

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun isBottomNavigationVisible() {
        onView(withId(R.id.main_bottom_navigation)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun isProgressBarInvisible() {
        onView(withId(R.id.home_progress_bar)).check(matches(withEffectiveVisibility(Visibility.GONE)))
    }
}