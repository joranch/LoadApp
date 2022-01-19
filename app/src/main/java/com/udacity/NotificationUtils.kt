package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

const val INTENT_EXTRA_STATUS_KEY = "status"
const val INTENT_EXTRA_FILE_KEY = "file"
const val REQUEST_CODE = 0

fun NotificationManager.sendNotification(
    channelId: String,
    messageBody: String,
    applicationContext: Context,
    fileName: String,
    status: String) {

    val detailIntent = Intent(applicationContext, DetailActivity::class.java)
    detailIntent.putExtra(INTENT_EXTRA_STATUS_KEY, status)
    detailIntent.putExtra(INTENT_EXTRA_FILE_KEY, fileName)

    val buttonPendingIntent = PendingIntent.getActivity(
        applicationContext,
        REQUEST_CODE,
        detailIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(applicationContext, channelId)
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            buttonPendingIntent)
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)

    notify(REQUEST_CODE, builder.build())
}