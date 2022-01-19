package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var selectedDownload = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        createNotificationChannel()

        custom_button.setOnClickListener {
            if (radio_group.checkedRadioButtonId == -1) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.toast_select_download_warning),
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                download(getSelectedUri())
            }
        }
    }

    // Help source: https://medium.com/@aungkyawmyint_26195/downloading-file-properly-in-android-d8cc28d25aca
    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id != downloadID)
                return

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            val cursor = downloadManager.query(DownloadManager.Query())

            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                var statusText = "Failure"
                if (DownloadManager.STATUS_SUCCESSFUL == status) {
                    statusText = "Success"
                }

                Toast.makeText(
                    applicationContext,
                    getString(R.string.notification_description),
                    Toast.LENGTH_LONG
                ).show()

                val notificationManager = getSystemService(NotificationManager::class.java)
                        as NotificationManager

                notificationManager.sendNotification(
                    CHANNEL_ID,
                    getString(R.string.notification_description),
                    applicationContext,
                    statusText,
                    selectedDownload
                )
            }
            custom_button.setCustomButtonState(ButtonState.Completed)
        }
    }

    private fun download(uri: String) {

        val request =
            DownloadManager.Request(Uri.parse(uri))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        custom_button.setCustomButtonState(ButtonState.Loading)

        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun getSelectedUri(): String {
        return when (radio_group.checkedRadioButtonId) {
            radio_glide.id -> {
                selectedDownload = GLIDE_FILEDESC
                GLIDE_URI
            }
            radio_loadapp.id -> {
                selectedDownload = LOADAPP_FILEDESC
                LOADAPP_URI
            }
            else -> {
                selectedDownload = RETROFIT_FILEDESC
                RETROFIT_URI
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.lightColor = Color.RED
            notificationChannel.description = CHANNEL_DESCTRIPTION
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        private const val GLIDE_URI = "https://github.com/bumptech/glide"
        private const val GLIDE_FILEDESC = "Glide repository"
        private const val LOADAPP_URI =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter"
        private const val LOADAPP_FILEDESC = "LoapApp repository"
        private const val RETROFIT_URI = "https://github.com/square/retrofit"
        private const val RETROFIT_FILEDESC = "Retrofit repository"
        private const val CHANNEL_ID = "channelId"
        private const val CHANNEL_NAME = "LoadApp notifications"
        private const val CHANNEL_DESCTRIPTION = "Notifications when a download completed"
    }
}
