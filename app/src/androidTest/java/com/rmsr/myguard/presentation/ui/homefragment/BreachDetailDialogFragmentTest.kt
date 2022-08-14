package com.rmsr.myguard.presentation.ui.homefragment

import androidx.compose.material.ExperimentalMaterialApi
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rmsr.myguard.presentation.ui.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BreachDetailDialogFragmentTest {
//        @Inject
//        lateinit var mainViewModel: MainViewModel

    @OptIn(ExperimentalMaterialApi::class)
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @OptIn(ExperimentalMaterialApi::class)
    @Before
    fun startMainActivity() {
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @Test
    fun testDialogFragmentUi() {
//        with(launchFragment<BreachDetailDialogFragment>()){
//            onFragment{ fragment ->
//                assertThat(,"fragment.dialog).isNotNull()
//            }
//        }
        val scenario = launchFragmentInContainer<BreachDetailDialogFragment>()
        scenario.recreate()
    }
}