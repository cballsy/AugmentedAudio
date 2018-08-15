package com.arqathon.glennreilly.augmentedaudio.service

import android.content.Intent
import android.app.IntentService
import android.support.v4.app.NotificationCompat
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.arqathon.glennreilly.augmentedaudio.R


class ActivityRecognizedService : IntentService {

    constructor() : super("ActivityRecognizedService") {}

    constructor(name: String) : super(name) {}

    override fun onHandleIntent(intent: Intent?) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            handleDetectedActivities(result.probableActivities)
        }
    }

    private fun handleDetectedActivities(probableActivities: List<DetectedActivity>) {
        for (activity in probableActivities) {
            when (activity.type) {
                DetectedActivity.IN_VEHICLE -> {
                    Log.e("ActivityRecogition", "In Vehicle: " + activity.confidence)
                }
                DetectedActivity.ON_BICYCLE -> {
                    Log.e("ActivityRecogition", "On Bicycle: " + activity.confidence)
                }
                DetectedActivity.ON_FOOT -> {
                    Log.e("ActivityRecogition", "On Foot: " + activity.confidence)
                }
                DetectedActivity.RUNNING -> {
                    Log.e("ActivityRecogition", "Running: " + activity.confidence)
                }
                DetectedActivity.STILL -> {
                    Log.e("ActivityRecogition", "Still: " + activity.confidence)
                }
                DetectedActivity.TILTING -> {
                    Log.e("ActivityRecogition", "Tilting: " + activity.confidence)
                }
                DetectedActivity.WALKING -> {
                    Log.e("ActivityRecogition", "Walking: " + activity.confidence)
                    if (activity.confidence >= 75) {
                        val builder = NotificationCompat.Builder(this)
                        builder.setContentText("Are you walking?")
                        builder.setSmallIcon(R.mipmap.ic_launcher)
                        builder.setContentTitle(getString(R.string.app_name))
                        NotificationManagerCompat.from(this).notify(0, builder.build())
                    }
                }
                DetectedActivity.UNKNOWN -> {
                    Log.e("ActivityRecogition", "Unknown: " + activity.confidence)
                }
            }
        }
    }
}

