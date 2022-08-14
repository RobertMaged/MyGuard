package com.rmsr.myguard.presentation.ui.settingsfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toDrawable
import androidx.fragment.app.clearFragmentResultListener
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.work.WorkManager
import com.google.android.material.color.MaterialColors
import com.rmsr.myguard.BuildConfig
import com.rmsr.myguard.R
import com.rmsr.myguard.domain.utils.ScanInterval
import com.rmsr.myguard.presentation.workers.ScheduleWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        //to avoid background issue
        view.setBackground(MaterialColors.getColor(view, R.attr.backgroundColor).toDrawable())

        return view
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {

        setPreferencesFromResource(R.xml.settings_screen, rootKey)

        val timePreference = findPreference<Preference>("Schedule_Check_Interval_Type_And_Time")
        val privacyPolicy = findPreference<Preference>("privacy_policy")
        val termsAndConditions = findPreference<Preference>("terms_and_conditions")

        findPreference<Preference>("app_version")?.let { renderAppVersion(it) }

        timePreference?.setOnPreferenceClickListener {
            setTimeListener()
            true
        }

        privacyPolicy?.setOnPreferenceClickListener {
            openWebViewDialog(PRIVACY_POLICY_URL)
            true
        }

        termsAndConditions?.setOnPreferenceClickListener {
            openWebViewDialog(TERMS_AND_CONDITIONS_URL)
            true
        }

    }

    private fun setTimeListener() {
        setFragmentResultListener(TIME_DIAG_REQUEST) { _, bundle ->
            val isTimeChanged =
                bundle.getBoolean(SchedulesCheckIntervalDialog.IS_CHECK_TIME_CHANGED, false)

            if (isTimeChanged) {
                val time = bundle.getInt(
                    SchedulesCheckIntervalDialog.DAYS_INTERVAL,
                    ScanInterval.FOUR_DAYS.inDays
                )

                val worker = WorkManager.getInstance(requireContext())
                ScheduleWorker.enqueuePeriodicWorkRequest(worker, time.toLong(), TimeUnit.DAYS)
            }

            clearFragmentResultListener(TIME_DIAG_REQUEST)
        }

        findNavController().navigate(
            SettingsFragmentDirections.actionSettingsFragmentToSchedulesCheckIntervalDialog()
        )
    }

    private fun openWebViewDialog(url: String) {
        val dialog = AlertDialog.Builder(requireContext())
        WebView.setWebContentsDebuggingEnabled(false)

        val webView = WebView(requireContext()).apply {
            settings.javaScriptEnabled = true
            webChromeClient = WebChromeClient()
            loadUrl(url)
        }


        dialog.setView(webView)

        dialog.setOnCancelListener {
            webView.stopLoading()
            webView.clearCache(true)
        }

        dialog.show()
    }


    private fun renderAppVersion(preference: Preference) {
        preference.summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }


    companion object {
        const val TIME_DIAG_REQUEST = "CHECK_TIME_DIAG_FRAGMENT"
        private const val PRIVACY_POLICY_URL =
            "https://www.privacypolicies.com/live/000726a4-4a02-4324-ab60-946d1b341e09"

        private const val TERMS_AND_CONDITIONS_URL =
            "https://www.privacypolicies.com/live/d3fb0cfd-0ce8-4613-a9fc-f0f3e0aa1bb7"
    }

}



