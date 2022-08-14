package com.rmsr.myguard.presentation.ui.schedules.detailfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.rmsr.myguard.presentation.ui.schedules.detailfragment.components.ScheduleDetailScreen
import com.rmsr.myguard.presentation.ui.schedules.mainfragment.components.MyGuard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScheduleDetailFragment : Fragment() {

    private val args by navArgs<ScheduleDetailFragmentArgs>()

    private val viewModel: ScheduleDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        when (args.detailType) {
            RequestedLeaks.ALL_LEAKS.name -> viewModel.loadScheduleAllLeaks(args.scheduleId)
            RequestedLeaks.NEW_LEAKS.name -> viewModel.loadScheduleNewLeaks(args.scheduleId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

        setContent {
            MyGuard {
                ScheduleDetailScreen(detailViewModel = viewModel)
            }
        }
    }

    companion object {
        @Keep
        enum class RequestedLeaks { NEW_LEAKS, ALL_LEAKS }
    }
}