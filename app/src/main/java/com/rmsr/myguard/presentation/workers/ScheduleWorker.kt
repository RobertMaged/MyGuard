package com.rmsr.myguard.presentation.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import androidx.work.rxjava3.RxWorker
import com.rmsr.myguard.BuildConfig
import com.rmsr.myguard.domain.entity.errors.ErrorEntity
import com.rmsr.myguard.domain.usecase.schedules.SchedulesScanUseCase
import com.rmsr.myguard.utils.MyLog
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Single
import java.util.concurrent.TimeUnit


@HiltWorker
class ScheduleWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val scanUseCase: SchedulesScanUseCase,
) : RxWorker(appContext, workerParams) {


    override fun createWork(): Single<Result> {

        return scanUseCase.invoke()

            .doOnSubscribe {
                MyLog.d(TAG, "starting Schedule Worker.", logThreadName = true)
            }
/*

            .flatMap { it: Result3<SessionId> ->
                return@flatMap when (it) {

                    //check done so send sessionId to NotifyWorker
                    is Result3.Success -> {
                        val sessionId = it.data.toLong()
                        MyLog.d(TAG, "scan done with sessionId: $sessionId.", logThreadName = true)

                        enqueueNotifyWorker(appContext, sessionId)
                        Maybe.just(Result.success(workDataOf("SESSION_ID" to sessionId)))
                    }

                    is Result3.Failure -> {
                        MyLog.e(TAG, "scan interrupted with some error: ${it.error}.",
                            logThreadName = true)

                        Maybe.just(Result.retry())
                    }
                }
            }

*/
            //check done so send sessionId to NotifyWorker
            .flatMap { sessionId ->
                MyLog.d(
                    TAG,
                    "scan done with sessionId: $sessionId.",
                    logThreadName = true
                )
                enqueueNotifyWorker(appContext, sessionId.value)
                return@flatMap Maybe.just(Result.success(workDataOf("SESSION_ID" to sessionId.value)))
            }


            .onErrorReturn { error ->
                MyLog.e(
                    TAG,
                    "scan interrupted with some error: ${error.message}.",
                    logThreadName = true
                )
                if (error is ErrorEntity.ApiError)
                    Result.failure()
                else
                    Result.retry()
            }

            .doOnComplete {
                MyLog.d(
                    TAG,
                    "scan done with NO sessionId: No Schedules saved.",
                    logThreadName = true
                )
            }
            //this means no Schedules is saved, so no check session created.
            .defaultIfEmpty(Result.success())

    }


    private fun enqueueNotifyWorker(context: Context, sessionId: Long): OneTimeWorkRequest {
        MyLog.d(TAG, "Creating NotifiedWorker.")
        return NotifiedWorker.createOneTimeWork(sessionId = sessionId).also {
            WorkManager.getInstance(context)
                .enqueueUniqueWork("notify", ExistingWorkPolicy.KEEP, it)
        }
    }

    companion object {
        private const val TAG = "Rob_ScheduleWorker"
        private const val CHANNEL_ID = "info2"
        private const val DEFAULT_INTERVAL_IN_DAYS = 4L

        fun enqueuePeriodicWorkIfNotExist(
            workManager: WorkManager,
        ): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            return PeriodicWorkRequestBuilder<ScheduleWorker>(
                DEFAULT_INTERVAL_IN_DAYS,
                TimeUnit.DAYS
            )
                .setInitialDelay(DEFAULT_INTERVAL_IN_DAYS, TimeUnit.DAYS)
                .setConstraints(constraints)
                .addTag("periodicLoop")
                .build()
                .apply {
                    workManager
                        .enqueueUniquePeriodicWork(
                            "periodicLoopWork",
                            ExistingPeriodicWorkPolicy.KEEP,
                            this
                        )
                }
        }

        fun enqueuePeriodicWorkRequest(
            workManager: WorkManager,
            repeatInterval: Long = DEFAULT_INTERVAL_IN_DAYS,
            repeatIntervalTimeUnit: TimeUnit = TimeUnit.DAYS,
        ): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            return PeriodicWorkRequestBuilder<ScheduleWorker>(
                repeatInterval,
                repeatIntervalTimeUnit
            )
                .apply {
                    if (BuildConfig.DEBUG.not()) setInitialDelay(
                        repeatInterval,
                        repeatIntervalTimeUnit
                    )
                }
                .setConstraints(constraints)
                .addTag("periodicLoop")
                .build()
                .apply {
                    workManager
                        .enqueueUniquePeriodicWork(
                            "periodicLoopWork",
                            ExistingPeriodicWorkPolicy.REPLACE,
                            this
                        )
                }

        }
    }
}

/*
       private fun sendBroadCast(context: Context, newLeaks: List<String>) {
           val intent = Intent(NotificationReceiver.SHOW_NOTIFICATION).apply {

               putExtra("new_leaks", newLeaks.size)
               putExtra("new_leaks_titles", newLeaks.toTypedArray())
           }
           LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
           // context.sendOrderedBroadcast(intent, "private")
       }

       private fun sendBroadCast(context: Context, notification: Notification, sessionId: Long) {
           val intent = Intent(NotificationReceiver.SHOW_NOTIFICATION)
               .putExtra("notification_id", 472)
               .putExtra("notification", notification)
               .putExtra(NotificationReceiver.EXTRA_SESSION_ID, sessionId)
           //.setClass(context, NotificationReceiver::class.java)

           val result = LocalBroadcastManager.getInstance(context).let {
               //fixme see why notification not registered in manifest.
               val filter = IntentFilter(NotificationReceiver.SHOW_NOTIFICATION)
               it.registerReceiver(NotificationReceiver(), filter)
               it.sendBroadcast(intent)
           }

           val x = 0
       }

       private fun createNotification(sessionId: Long, newLeaks: List<String>): Notification {

           createChannel()

           val args = SchedulesMainFragmentArgs.fromBundle(bundleOf("sessionId" to sessionId))
           val tapActionPendingIntent = NavDeepLinkBuilder(applicationContext)
               .setGraph(R.navigation.nav_graph)
               .setDestination(R.id.scheduleFragment)
               .setArguments(args.toBundle())
               .createPendingIntent()

           val markNotifiedIntent = Intent(appContext, ActionReceiver::class.java).apply {
               action = ActionReceiver.GOT_IT_ACTION
   //            putExtra(ActionReceiver.EXTRA_SESSION_ID, sessionId)
           }

           val notifiedPendingIntent = PendingIntent.getBroadcast(appContext,
               0,
               markNotifiedIntent,
               PendingIntent.FLAG_CANCEL_CURRENT)


           val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
               .setSmallIcon(R.mipmap.ic_launcher_round_binoculars)
   //            .setLargeIcon(BitmapFactory.decodeResource(appContext.resources, R.mipmap.ic_launcher_round_binoculars))
               .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
               .setContentTitle("MyGuard")
               .setContentText("Some of your emails have seen in ${newLeaks.size} new leeks\n $newLeaks")
               .setDefaults(NotificationCompat.DEFAULT_SOUND or NotificationCompat.DEFAULT_VIBRATE)
               .setCategory(NotificationCompat.CATEGORY_REMINDER)
               .setContentIntent(tapActionPendingIntent)
               .addAction(R.drawable.ic_binoculars, "Got it", notifiedPendingIntent)
               .setAutoCancel(true)

           // NotificationManagerCompat.from(appContext).notify(472, notification.build())
           return notification.build()
       }
   */