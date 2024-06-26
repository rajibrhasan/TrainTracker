package com.example.location.Util.ActivityRecognizer

import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.location.R
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity
import java.util.*

@Suppress("DEPRECATION")
class ActivityDetectionIntentService : IntentService(ActivityDetectionIntentService::class.simpleName) {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d("Rajib","Inside activity detect")

        val builder = NotificationCompat.Builder(this, "channel_id2")
            .setSmallIcon(R.drawable.train2)
            .setContentTitle("Acitvity is being tracked.")
            .setContentText("Location is being updated.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("channel_id2", "Location Update", NotificationManager.IMPORTANCE_LOW)
            channel.description = "This channel is used to notify users about location update."
            notificationManager.createNotificationChannel(channel)
        }

        startForeground(2,builder.build())




        if(ActivityTransitionResult.hasResult(intent)){
            val result = ActivityTransitionResult.extractResult(intent!!)
            result?.transitionEvents?.forEach { event->
                val intent = Intent(Constants.BROADCAST_DETECTED_ACTIVITY)
                val activity = ActivityTransitionsUtil.toActivityString(event.activityType)
                val transition = ActivityTransitionsUtil.toTransitionType(event.transitionType)
                intent.putExtra("activity",activity )
                intent.putExtra("transition",transition)
                builder.setContentText("$activity $transition")
                notificationManager.notify(2,builder.build())
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
                Log.d("Rajib",event.toString())
            }
        }
    }
}