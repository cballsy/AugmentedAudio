package com.arqathon.glennreilly.augmentedaudio.service

import android.content.Intent
import android.app.IntentService
import android.content.Context
import android.support.v4.app.NotificationCompat
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.location.DetectedActivity
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.arqathon.glennreilly.augmentedaudio.R
import com.arqathon.glennreilly.augmentedaudio.MainActivity
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class ActivityRecognitionService : IntentService {

    constructor() : super("ActivityRecognizedService") {}

    constructor(name: String) : super(name)

    companion object {
        val POSSIBLE_ACTIVITIES = intArrayOf(
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
        )

        fun getActivityString(context: Context, detectedActivityType: Int): String {
            val resources = context.getResources()
            when (detectedActivityType) {
                DetectedActivity.ON_BICYCLE -> return resources.getString(R.string.bicycle)
                DetectedActivity.ON_FOOT -> return resources.getString(R.string.foot)
                DetectedActivity.RUNNING -> return resources.getString(R.string.running)
                DetectedActivity.STILL -> return resources.getString(R.string.still)
                DetectedActivity.TILTING -> return resources.getString(R.string.tilting)
                DetectedActivity.WALKING -> return resources.getString(R.string.walking)
                DetectedActivity.IN_VEHICLE -> return resources.getString(R.string.vehicle)
                else -> return resources.getString(R.string.unknown_activity, detectedActivityType.toString())
            }
        }

        fun getMostProbableActivityFromJson(jsonActivity: String): DetectedActivity? {
            return Gson().fromJson(jsonActivity, DetectedActivity::class.java)
        }

        fun detectedActivitiesFromJson(jsonArray: String): ArrayList<DetectedActivity> {
            val listType = object : TypeToken<ArrayList<DetectedActivity>>() {}.type
            var detectedActivities: ArrayList<DetectedActivity>? = Gson().fromJson(jsonArray, listType)
            if (detectedActivities == null) {
                detectedActivities = ArrayList()
            }
            return detectedActivities
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            val result = ActivityRecognitionResult.extractResult(intent)
            //handleDetectedActivities(result.probableActivities)

            val mostProbableActivity = result.mostProbableActivity
            val confidence = mostProbableActivity.confidence
            val activityType = mostProbableActivity.type

            val detectedActivities = result.probableActivities as ArrayList
            PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString(
                    MainActivity.DETECTED_ACTIVITY,
                    detectedActivitiesToJson(detectedActivities)
                )

                .putString(
                    MainActivity.MOST_PROBABLE_ACTIVITY,
                    Gson().toJson(mostProbableActivity)
                )
                .apply()
        }
    }


    fun detectedActivitiesToJson(detectedActivitiesList: ArrayList<DetectedActivity>): String {
        val type = object : TypeToken<ArrayList<DetectedActivity>>() {

        }.type
        return Gson().toJson(detectedActivitiesList, type)
    }

}

