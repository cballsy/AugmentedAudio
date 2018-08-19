package com.arqathon.glennreilly.augmentedaudio

import java.util.ArrayList
import java.util.HashMap
import android.widget.ArrayAdapter
import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import com.arqathon.glennreilly.augmentedaudio.service.ActivityRecognitionService
import com.google.android.gms.location.DetectedActivity

internal class ActivitiesAdapter(context: Context,
                                 detectedActivities: ArrayList<DetectedActivity>) : ArrayAdapter<DetectedActivity>(context, 0, detectedActivities) {
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val detectedActivity = getItem(position)
        var view = view
        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.detected_activity, parent, false)
        }

        val activityName = view!!.findViewById<View>(R.id.activity_type) as TextView
        val activityConfidenceLevel = view.findViewById(R.id.confidence_percentage) as TextView

        detectedActivity?.let {
            activityName.text = ActivityRecognitionService.getActivityString(context,
                    detectedActivity.type)
            activityConfidenceLevel.text = context.getString(R.string.percentage,
                    detectedActivity.confidence)
        }

        return view
    }

    fun updateActivities(detectedActivities: ArrayList<DetectedActivity>) {
        val detectedActivitiesMap = HashMap<Int, Int>()
        for (activity in detectedActivities) {
            detectedActivitiesMap[activity.type] = activity.confidence
        }

        val temporaryList = ArrayList<DetectedActivity>()

        ActivityRecognitionService.POSSIBLE_ACTIVITIES.forEach {
            val confidence = if (detectedActivitiesMap.containsKey(it)) detectedActivitiesMap[it] ?: 0 else 0
            temporaryList.add(DetectedActivity(it, confidence))
        }
        this.clear()

        for (detectedActivity in temporaryList) {
            this.add(detectedActivity)
        }
    }
}