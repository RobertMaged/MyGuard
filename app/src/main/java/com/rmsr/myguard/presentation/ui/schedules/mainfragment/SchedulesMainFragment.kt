package com.rmsr.myguard.presentation.ui.schedules.mainfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.VisibleForTesting
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.rmsr.myguard.presentation.ui.schedules.mainfragment.components.MyGuard
import com.rmsr.myguard.presentation.ui.schedules.mainfragment.components.ScheduleScreen
import dagger.hilt.android.AndroidEntryPoint

@OptIn(ExperimentalMaterialApi::class)
@AndroidEntryPoint
class SchedulesMainFragment : Fragment() {


    @VisibleForTesting
    val mViewModel: SchedulesViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                MyGuard {
                    ScheduleScreen(viewModel = mViewModel, navController = findNavController())
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        navArgs<SchedulesMainFragmentArgs>().value.sessionId
            .takeIf { it > 0L }?.run { mViewModel.markSessionUserResponded(sessionId = this) }
    }


    companion object {
        private const val TAG = "Rob_ScheduleCheckFrag"
        const val TIME_DIAG_REQUEST = "CHECK_TIME_DIAG_FRAGMENT"
    }
}