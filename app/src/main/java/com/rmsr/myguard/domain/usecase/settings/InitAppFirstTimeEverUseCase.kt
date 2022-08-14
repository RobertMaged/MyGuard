package com.rmsr.myguard.domain.usecase.settings

import com.rmsr.myguard.domain.repository.Settings
import javax.inject.Inject

class InitAppFirstTimeEverUseCase @Inject constructor(
    private val settings: Settings,
) {

    private val isNotFirstTime
        get() = settings.isFirstAppRun.not()


    /**
     * Invoke given [initBlock] if its first time for App to be opened.
     *
     * This method is **One Time Life** called if [initBlock] result is `true`.
     *
     * @param initBlock what needed to be initialized, if invocation result is **`true`**
     * this use case will be useless next times its been called, if **`false`** it will try to
     * invoke given [initBlock] every call until its success.
     *
     * @return `true` if initialization done successfully,
     * `false` if its not first time or [initBlock] not succeed.
     */
    operator fun invoke(initBlock: () -> Boolean): Boolean {
        if (isNotFirstTime) return false

        val isSuccessful = initBlock.invoke()
        if (isSuccessful.not()) return false


        changeStateToNotFirstTime()
        return true
    }

    private fun changeStateToNotFirstTime() {
        settings.isFirstAppRun = false
    }


}

