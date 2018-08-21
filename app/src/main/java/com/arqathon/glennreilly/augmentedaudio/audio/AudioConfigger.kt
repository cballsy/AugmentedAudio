package com.arqathon.glennreilly.augmentedaudio.audio

import com.arqathon.glennreilly.augmentedaudio.R
import com.google.android.gms.location.DetectedActivity

object AudioConfigger {
    val soundMap = mapOf(
        DetectedActivity.STILL to R.raw.beep_a_major,
        DetectedActivity.ON_FOOT to R.raw.one_shot_tom,
        DetectedActivity.WALKING to R.raw.ping_bing_e_major,
        DetectedActivity.RUNNING to R.raw.beep_a_major,
        DetectedActivity.IN_VEHICLE to R.raw.beep_a_major,
        DetectedActivity.ON_BICYCLE to R.raw.beep_a_major,
        DetectedActivity.TILTING to R.raw.beep_a_major,
        DetectedActivity.UNKNOWN to R.raw.beep_a_major
    )

}
