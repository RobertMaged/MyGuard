package com.rmsr.myguard.presentation.ui.settingsfragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TimePicker
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.rmsr.myguard.BuildConfig
import com.rmsr.myguard.R
import com.rmsr.myguard.databinding.FragmentCheckTimeDiagBinding
import com.rmsr.myguard.domain.usecase.settings.GetPreferredScanIntervalUseCase
import com.rmsr.myguard.domain.usecase.settings.SetPreferredScanIntervalUseCase
import com.rmsr.myguard.domain.utils.ScanInterval
import com.rmsr.myguard.presentation.ui.schedules.mainfragment.SchedulesMainFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SchedulesCheckIntervalDialog @Inject constructor(
    private val getInterval: GetPreferredScanIntervalUseCase,
    private val setInterval: SetPreferredScanIntervalUseCase
) : DialogFragment() {

    private lateinit var intervalRadioGroup: RadioGroup
    private lateinit var timePicker: TimePicker


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = FragmentCheckTimeDiagBinding.inflate(layoutInflater, null, false)

        intervalRadioGroup = binding.checkTimeDiagRadioGroup
        timePicker = binding.checkTimeDiagTimePicker.apply {
            if (BuildConfig.DEBUG.not())
                (this.parent as? LinearLayout)?.visibility = View.GONE
        }

        val savedCheckInterval = getInterval.invoke()

        updateUi(savedCheckInterval)

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.settings_schedules_scan_interval))
            .setView(binding.root)
            .setPositiveButton("Ok") { _, _ ->

                //only data changed save new time
                getUserSelectedInterval().takeIf { it != savedCheckInterval }
                    ?.run {
                        setInterval.invoke(this)

                        //send changed true to parent to deal with worker
                        val bundle = bundleOf(
                            IS_CHECK_TIME_CHANGED to true,
                            DAYS_INTERVAL to this.inDays
                        )
                        setFragmentResult(SchedulesMainFragment.TIME_DIAG_REQUEST, bundle)
                        setFragmentResult(SettingsFragment.TIME_DIAG_REQUEST, bundle)
                    }

            }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()


    }


    private fun getUserSelectedInterval(radioGroup: RadioGroup = intervalRadioGroup): ScanInterval =
        when (radioGroup.checkedRadioButtonId) {
            R.id.check_time_diag_radio_daily -> ScanInterval.DAILY
            R.id.check_time_diag_radio_two_weeks -> ScanInterval.TWO_WEEKS
            R.id.check_time_diag_radio_monthly -> ScanInterval.MONTHLY
            else -> ScanInterval.FOUR_DAYS
        }

    private fun updateUi(scanInterval: ScanInterval) {

        @IdRes val savedDays = when (scanInterval) {
            ScanInterval.DAILY -> R.id.check_time_diag_radio_daily
            ScanInterval.TWO_WEEKS -> R.id.check_time_diag_radio_two_weeks
            ScanInterval.MONTHLY -> R.id.check_time_diag_radio_monthly
            else -> R.id.check_time_diag_radio_four_days
        }

        intervalRadioGroup.check(savedDays)


    }


    companion object {
        private const val TAG = "rob_CheckDiagFragment"
        const val IS_CHECK_TIME_CHANGED = "is_schedule_check_time_changed"
        const val DAYS_INTERVAL = "days_interval"
        const val AT_TIME_INTERVAL = "at_clock_time_interval"
    }

}
/*@AndroidEntryPoint
class CheckTimeDiagFragment @Inject constructor (
    private val getInterval: GetPreferredScanIntervalUseCase,
    private val setInterval: SetPreferredScanIntervalUseCase
        ) : DialogFragment() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var timePicker: TimePicker

    @Inject
    lateinit var pref: SettingPreferences

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = FragmentCheckTimeDiagBinding.inflate(layoutInflater, null, false)

        radioGroup = binding.checkTimeDiagRadioGroup
        timePicker = binding.checkTimeDiagTimePicker.apply {
            if (BuildConfig.DEBUG.not())
                (this.parent as? LinearLayout)?.visibility = View.GONE
        }

//        pref.init(requireContext())

        //discoveredDate is stored in format "days:hour:min"
        val savedCheckInterval = pref.scheduleCheckInterval

        updateUi(savedCheckInterval)

        return AlertDialog.Builder(requireContext())
            .setTitle("Schedule Check Time")
            .setView(binding.root)
            .setPositiveButton("Ok") { _, _ ->

                //only data changed save new time
                getUserCheckTimeFormatted().takeIf { it != savedCheckInterval }
                    ?.let {
                        pref.scheduleCheckInterval = it

                        //send changed true to parent to deal with worker
                        val bundle = Bundle()
                        bundle.putBoolean(IS_CHECK_TIME_CHANGED, true)
                        bundle.putString(DAYS_INTERVAL, it.split(":", limit = 2)[0])
                        bundle.putString(AT_TIME_INTERVAL, it.split(":", limit = 2)[1])
                        setFragmentResult(SchedulesMainFragment.TIME_DIAG_REQUEST, bundle)
                        setFragmentResult(SettingsFragment.TIME_DIAG_REQUEST, bundle)
                    }

            }
            .setNegativeButton("Cancel") { _, _ -> }
            .create()


    }


    private fun getUserCheckTimeFormatted(): String {
        val selectedDaysInterval = when (radioGroup.checkedRadioButtonId) {
            R.id.check_time_diag_radio_daily -> ScanInterval.DAILY.inDays
            R.id.check_time_diag_radio_two_weeks -> ScanInterval.TWO_WEEKS.inDays
            R.id.check_time_diag_radio_monthly -> ScanInterval.MONTHLY.inDays
            else -> ScanInterval.FOUR_DAYS.inDays
        }

//        pref.SchScheduleCheckIntervalTime =
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            "$selectedDaysInterval:${timePicker.hour}:${timePicker.minute}"
        else
            "$selectedDaysInterval:${timePicker.currentHour}:${timePicker.currentMinute}"
    }

    private fun updateUi(savedCheckInterval: String) {
        //discoveredDate is stored in format "days:hour:min"
        val savedSplited = savedCheckInterval.split(":")

        @IdRes val savedDays = when (savedSplited[0]) {
            ScanInterval.DAILY.inDays -> R.id.check_time_diag_radio_daily
            ScanInterval.TWO_WEEKS.inDays -> R.id.check_time_diag_radio_two_weeks
            ScanInterval.MONTHLY.inDays -> R.id.check_time_diag_radio_monthly
            else -> R.id.check_time_diag_radio_weekly
        }

        radioGroup.check(savedDays)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            timePicker.hour = savedSplited[1].toInt()
            timePicker.minute = savedSplited[2].toInt()
        } else {
            timePicker.currentHour = savedSplited[1].toInt()
            timePicker.currentMinute = savedSplited[2].toInt()
        }
    }


    companion object {
        private const val TAG = "rob_CheckDiagFragment"
        const val IS_CHECK_TIME_CHANGED = "is_schedule_check_time_changed"
        const val DAYS_INTERVAL = "days_interval"
        const val AT_TIME_INTERVAL = "at_clock_time_interval"
    }

}*/