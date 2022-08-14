package com.rmsr.myguard.presentation.workers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        context ?: return

        NotificationManagerCompat.from(context).cancel(NotifiedWorker.NOTIFICATION_ID)
    }

    companion object {
        private const val TAG = "Rob_ActionReceiver"
        const val GOT_IT_ACTION =
            "com.rmsr.myguard.presentation.workers.ActionReceiver.GOT_IT_ACTION"
    }
}