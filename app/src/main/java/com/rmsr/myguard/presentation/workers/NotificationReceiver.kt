package com.rmsr.myguard.presentation.workers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.rmsr.myguard.R
import com.rmsr.myguard.utils.MyLog


@Deprecated("")
class NotificationReceiver : BroadcastReceiver() {


    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null)
            return


        val id = intent.getIntExtra("notification_id", 0)
        val notification = intent.getParcelableExtra<Notification>("notification") ?: return

        MyLog.d(
            TAG,
            "context and intent are safe, action is: ${intent.action}, and notificationId is: $id",
            logThreadName = true
        )

        showNotification(context, id, notification)

        val sessionId = intent.getLongExtra(EXTRA_SESSION_ID, -1)
        if (sessionId == -1L) return

        MyLog.d(TAG, "Creating NotifiedWorker.")
        NotifiedWorker.createOneTimeWork(sessionId = sessionId).run {
            WorkManager.getInstance(context)
                .enqueueUniqueWork("notify", ExistingWorkPolicy.KEEP, this)
        }

    }

//    PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
//    long futureInMillis = SystemClock. elapsedRealtime () + delay ;
//    AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
//    assert alarmManager != null;
//    alarmManager.set(AlarmManager. ELAPSED_REALTIME_WAKEUP , futureInMillis , pendingIntent) ;

    private fun showNotification(
        context: Context,
        notificationId: Int,
        notification: Notification
    ) {
        MyLog.d(TAG, "Here is the notification coming.", logThreadName = true)
        NotificationManagerCompat.from(context).cancel(notificationId)
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }


    //            sendNotification(context, intent?.getIntExtra("new_leaks", 0) ?: 0,
//                intent?.getStringArrayExtra("new_leaks_titles")?: emptyArray()
//                )
    private fun sendNotification(appContext: Context, newLeaks: Int, titles: Array<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(CHANNEL_ID, "New leaks", NotificationManager.IMPORTANCE_DEFAULT)
                    .apply {
                        description = "New leaks"
                        enableLights(true)
                        lightColor = Color.GREEN
                    }
            val notificationManager =
                appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val pendingIntent = NavDeepLinkBuilder(appContext)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.scheduleFragment)
            .createPendingIntent()

        val notification = NotificationCompat.Builder(appContext, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round_binoculars)
//            .setLargeIcon(BitmapFactory.decodeResource(appContext.resources, R.mipmap.ic_launcher_round_binoculars))
//            .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
            .setContentTitle("MyGuard")
            .setContentText("Some of your emails have seen in $newLeaks new leeks\n $titles")
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        NotificationManagerCompat.from(appContext).notify(472, notification.build())
    }

    companion object {
        private const val TAG = "Rob_NotificationReceiver"
        const val SHOW_NOTIFICATION =
            "com.rmsr.myguard.presentation.workers.SHOW_CHECK_NOTIFICATION"
        const val EXTRA_SESSION_ID =
            "com.rmsr.myguard.presentation.workers.NotificationReceiver.EXTRA_SESSION_ID"
        private const val CHANNEL_ID = "info2"
    }
}