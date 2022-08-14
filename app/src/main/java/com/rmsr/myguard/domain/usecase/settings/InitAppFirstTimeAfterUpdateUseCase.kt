package com.rmsr.myguard.domain.usecase.settings

import com.rmsr.myguard.BuildConfig
import com.rmsr.myguard.domain.repository.Settings
import javax.inject.Inject

class InitAppFirstTimeAfterUpdateUseCase @Inject constructor(
    private val settings: Settings,
) {

    private val isSameAppVersion
        get() = settings.appVersion == BuildConfig.VERSION_CODE

    /**
     * Invoke given [initBlock] if its first time for App to be opened after app update.
     *
     * This method is **One Time Life per app version** called if [initBlock] result is `true`.
     *
     * @param initBlock what needed to be initialized, if invocation result is **`true`**
     * this use case will be useless next times its been called until next app update, if **`false`** it will try to
     * invoke given [initBlock] every call until its success.
     *
     * @return `true` if initialization done successfully,
     * `false` if its not first time after update or [initBlock] not succeed.
     */
    operator fun invoke(initBlock: () -> Boolean): Boolean {
        if (isSameAppVersion) return false

        val isSuccessful = initBlock.invoke()
        if (isSuccessful.not()) return false


        updateAppVersion()
        return true
    }

    private fun updateAppVersion() {
        settings.appVersion = BuildConfig.VERSION_CODE
    }


}