package com.rmsr.myguard.presentation.workers

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationChannelGroupCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.os.bundleOf
import androidx.hilt.work.HiltWorker
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.*
import androidx.work.rxjava3.RxWorker
import com.rmsr.myguard.R
import com.rmsr.myguard.domain.entity.SessionId
import com.rmsr.myguard.domain.usecase.breaches.GetSessionUnNotifiedLeaksUseCase
import com.rmsr.myguard.domain.usecase.session.MarkSessionRecordsNotifiedUseCase
import com.rmsr.myguard.domain.usecase.settings.IsOnNewsOnlyNotifyPreferredUseCase
import com.rmsr.myguard.presentation.ui.schedules.mainfragment.SchedulesMainFragmentArgs
import com.rmsr.myguard.utils.MyLog
import dagger.Module
import dagger.Provides
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.multibindings.IntoSet
import io.reactivex.rxjava3.core.Single


@HiltWorker
class NotifiedWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val newLeaksUseCase: GetSessionUnNotifiedLeaksUseCase,
    private val notifiedUseCase: MarkSessionRecordsNotifiedUseCase,
    private val isOnNewsOnly: dagger.Lazy<IsOnNewsOnlyNotifyPreferredUseCase>,
) : RxWorker(appContext, workerParams) {


    override fun createWork(): Single<Result> {
        val keyNotExist = inputData.hasKeyWithValueOfType<Long>(SESSION_ID).not()
        val notifyDisabled =
            NotificationManagerCompat.from(applicationContext).areNotificationsEnabled().not()

        if (keyNotExist || notifyDisabled) return Single.just(Result.failure())

        val sessionId = SessionId(inputData.getLong(SESSION_ID, -1))

        MyLog.d(
            TAG,
            "starting Notified Worker with sessionId input: $sessionId",
            logThreadName = true
        )

        return newLeaksUseCase.invoke(sessionId)


            .defaultIfEmpty(emptyList())
            .flatMapCompletable { breaches ->
                val newLeaksNames = breaches.map { it.title }

                cookNotification(sessionId, newLeaksNames)?.also(::showNotification)

                return@flatMapCompletable notifiedUseCase.invoke(sessionId = sessionId)
            }

            .toSingleDefault(Result.success())

            .onErrorResumeNext {
                MyLog.e(TAG, "marked notified error: ${it.message}", logThreadName = true)

                return@onErrorResumeNext Single.just(
                    if (sessionId.value != -1L) Result.retry() else Result.failure()
                )
            }
    }

    private fun showNotification(notification: Notification) {
        NotificationManagerCompat.from(applicationContext).run {
            cancel(NOTIFICATION_ID)
            notify(NOTIFICATION_ID, notification)
        }
    }

    /**
     * @return Null if user Wanted to be notified on new leaks only.
     */
    private fun cookNotification(sessionId: SessionId, newLeaks: List<String>): Notification? {

        //if user didn't want to notified until new leaks found
        if (newLeaks.isEmpty() && isOnNewsOnly.get().invoke()) return null

        val args = SchedulesMainFragmentArgs.fromBundle(bundleOf("sessionId" to sessionId.value))
        val tapActionPendingIntent = NavDeepLinkBuilder(applicationContext)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.scheduleFragment)
            .setArguments(args.toBundle())
            .createPendingIntent()

        val markNotifiedIntent =
            Intent(applicationContext, NotificationActionReceiver::class.java).apply {
                action = NotificationActionReceiver.GOT_IT_ACTION
//            putExtra(ActionReceiver.EXTRA_SESSION_ID, sessionId)
            }

        val notifiedPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            markNotifiedIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )


        val notification = NotificationCompat.Builder(
            applicationContext,
            SchedulesChannelsModule.NEW_LEAKS_CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher_round_binoculars)
//            .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, R.mipmap.ic_launcher_round_binoculars))
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setContentTitle("MyGuard")
            .setContentText(
                if (newLeaks.isEmpty()) "Schedules scanned, no new leaks found."
                else "Some of your Schedules have seen in ${newLeaks.size} new leaks."
            )
            .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(tapActionPendingIntent)
            .addAction(R.drawable.ic_binoculars, "Got it", notifiedPendingIntent)
            .setAutoCancel(true)

        return notification.build()
    }

    companion object {
        const val SESSION_ID = "SESSION_ID"
        const val NOTIFICATION_ID = 472
        private const val CHANNEL_ID = "info2"
        private const val TAG = "Rob_NotifiedWorker"

        fun getChannel() = NotificationChannelCompat.Builder(
            CHANNEL_ID,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        )
            .setName("New leaks")
            .setDescription("New leaks")
            .setLightsEnabled(true)
            .setLightColor(Color.GREEN)
            .setVibrationEnabled(false)
            .build()

        fun createOneTimeWork(sessionId: Long): OneTimeWorkRequest {
            val data = workDataOf(SESSION_ID to sessionId)

            return OneTimeWorkRequestBuilder<NotifiedWorker>().setInputData(data).build()
        }

    }
}


@Module
@InstallIn(ViewModelComponent::class)
object SchedulesChannelsModule {
    const val NEW_LEAKS_CHANNEL_ID = "info2"

    private const val SCHEDULES_GROUP_CHANNEL_ID = "Schedules"

    @Provides
    @ViewModelScoped
    @IntoSet
    fun provideNewLeaksNotificationChannel() = NotificationChannelCompat.Builder(
        NEW_LEAKS_CHANNEL_ID,
        NotificationManagerCompat.IMPORTANCE_DEFAULT
    )
        .setName("New leaks")
        .setDescription("New leaks")
        .setLightsEnabled(true)
        .setLightColor(Color.GREEN)
        .setVibrationEnabled(false)
        .setGroup(SCHEDULES_GROUP_CHANNEL_ID)
        .build()


    @Provides
    @ViewModelScoped
    @IntoSet
    fun provideSchedulesNotificationChannelGroup() =
        NotificationChannelGroupCompat.Builder(
            SCHEDULES_GROUP_CHANNEL_ID
        )
            .setName("Schedules")
            .build()
}