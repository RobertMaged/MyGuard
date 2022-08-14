package com.rmsr.myguard.presentation.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.rmsr.myguard.presentation.ui.schedules._ScheduleAdapter
import com.rmsr.myguard.presentation.ui.schedules.mainfragment.SchedulesMainFragment
import javax.inject.Inject

class FragmentsFactoryForTest @Inject constructor(
    private val scheduleAdapter: dagger.Lazy<_ScheduleAdapter>
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return super.instantiate(classLoader, className)
        return when (className) {
            SchedulesMainFragment::class.java.name -> SchedulesMainFragment(/*scheduleCheckAdapter = scheduleAdapter.get()*/)
            else -> super.instantiate(classLoader, className)
        }
    }
}