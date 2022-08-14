package com.rmsr.myguard.presentation.ui

import android.animation.Animator
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.material.ExperimentalMaterialApi
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.color.MaterialColors
import com.google.android.material.textview.MaterialTextView
import com.rmsr.myguard.R
import com.rmsr.myguard.databinding.ActivityMainBinding
import com.rmsr.myguard.domain.utils.NetworkStatus
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val TAG = "Rob_MainActivity"

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()


    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = getMainFactory()

        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_MyGuard)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.emailFragment,
            R.id.passwordFragment,
            R.id.scheduleFragment
        ).build()

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        NavigationUI.setupWithNavController(
            navigationBarView = binding.mainBottomNavigation,
            navController = navController
        )


        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.connectionState.collectLatest { status ->

                    val isConnectionActive = when (status) {
                        NetworkStatus.Offline -> false
                        NetworkStatus.Online -> true
                        else -> return@collectLatest
                    }

                    binding.connectionState.makeResult(isConnectionActive)
                    binding.connectionState.animateConnectionState(isConnectionActive)
                }
            }
        }

    }

    private fun getMainFactory(): MainNavGraphFragmentsFactory = EntryPointAccessors.fromActivity(
        this@MainActivity, MainNavGraphFragmentsFactoryEntryPoint::class.java
    ).getFragmentFactory()


    override fun onSupportNavigateUp(): Boolean {
        return (NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp())
    }

    private fun MaterialTextView.animateConnectionState(isActive: Boolean) {

        animate().apply {

            translationY(0f)
//                duration = 2000
            startDelay = 2000
            setListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {

                    visibility = if (isActive) View.GONE else View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator?) = Unit
                override fun onAnimationCancel(animation: Animator?) = Unit
                override fun onAnimationRepeat(animation: Animator?) = Unit

            })

        }

    }

    private fun MaterialTextView.makeResult(isActive: Boolean) {
        if (isActive) {
            text = context.getString(R.string.main_activity_online_network_state)
            background =
                AppCompatResources.getDrawable(context, R.color.success_green_color)
        } else {
            text = context.getString(R.string.main_activity_offline_network_state)
            background = MaterialColors.getColor(this, R.attr.colorError).toDrawable()
        }
    }
}