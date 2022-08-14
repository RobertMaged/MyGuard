package com.rmsr.myguard.presentation.util

import android.content.Context
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationManagerCompat
import com.rmsr.myguard.utils.MyLog
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Inject

@ViewModelScoped
open class AppNotificationsChannelsManager @Inject constructor(
    protected val channels: Set<@JvmSuppressWildcards NotificationChannelCompat>,
    protected val channelsGroups: Set<@JvmSuppressWildcards NotificationChannelGroupCompat>
) {

    open fun initAll(context: Context) = with(NotificationManagerCompat.from(context)) {
        MyLog.d(
            "Rob_AppNotificationsManager",
            "init: ${channels.size} channels, and ${channelsGroups.size} groups."
        )

        createNotificationChannelGroupsCompat(channelsGroups.toList())
        createNotificationChannelsCompat(channels.toList())
    }
}