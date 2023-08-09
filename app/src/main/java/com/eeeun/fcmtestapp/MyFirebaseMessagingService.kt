package com.eeeun.fcmtestapp

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val TAG = "MessagingService"
        private const val CHANNEL_NAME = "Push Notification"
        private const val CHANNEL_DESCRIPTION = "Push Notification 을 위한 채널"
        private const val CHANNEL_ID = "Channel Id"
    }

    /* 토큰 생성 메서드 */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    /* 메세지 수신 메서드 */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification!!.title
        val message = remoteMessage.notification!!.body

        Log.d(TAG, "onMessageReceived() - remoteMessage : $remoteMessage")
        Log.d(TAG, "onMessageReceived() - from : ${remoteMessage.from}")
        Log.d(TAG, "onMessageReceived() - notification : ${remoteMessage.notification?.body}")
        Log.d(TAG, "onMessageReceived() - title : $title")
        Log.d(TAG, "onMessageReceived() - message : $message")

        sendNotification(title, message)
    }

    /* 알림 생성 메서드 */
    private fun sendNotification(
        title: String?,
        message: String?
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Oreo(26) 이상 버전에는 channel 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = CHANNEL_DESCRIPTION
            notificationManager.createNotificationChannel(channel)
        }

        //알림 생성을 위한 권한(POST_NOTIFICATIONS)이 있는지 확인, 없으면 return
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return
        }

        NotificationManagerCompat.from(this)
            .notify((System.currentTimeMillis()/100).toInt(), createNotification(title, message))
    }

    /* 알림 설정 메서드 */
    private fun createNotification(
        title: String?,
        message: String?
    ): Notification {
        val id = 0
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)  //알림 눌렀을 때 실행할 Intent 설정
            .setAutoCancel(true)  //클릭 시 자동으로 삭제되도록 설정

        return notificationBuilder.build()
    }

}