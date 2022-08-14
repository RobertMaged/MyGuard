package com.rmsr.myguard.presentation.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.rmsr.myguard.domain.usecase.settings.GetPreferredScanIntervalUseCase
import com.rmsr.myguard.domain.usecase.settings.SetPreferredScanIntervalUseCase
import com.rmsr.myguard.presentation.ui.settingsfragment.SchedulesCheckIntervalDialog
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

@EntryPoint
@InstallIn(ActivityComponent::class)
interface MainNavGraphFragmentsFactoryEntryPoint {
    fun getFragmentFactory(): MainNavGraphFragmentsFactory
}

class MainNavGraphFragmentsFactory @Inject constructor(
    private val getScanInterval: dagger.Lazy<GetPreferredScanIntervalUseCase>,
    private val setScanInterval: dagger.Lazy<SetPreferredScanIntervalUseCase>
) : FragmentFactory() {

    //    @Inject
//    lateinit var scheduleAdapter: dagger.Lazy<ScheduleAdapter>
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            SchedulesCheckIntervalDialog::class.java.name -> SchedulesCheckIntervalDialog(
                getScanInterval.get(),
                setScanInterval.get()
            )
            else -> super.instantiate(classLoader, className)
        }
    }
}