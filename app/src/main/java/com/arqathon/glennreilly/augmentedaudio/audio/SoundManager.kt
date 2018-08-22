package com.arqathon.glennreilly.augmentedaudio.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.arqathon.glennreilly.augmentedaudio.R
import com.arqathon.glennreilly.augmentedaudio.data.ActivityNotificationEvent
import com.google.android.gms.location.DetectedActivity

object SoundManager {
    private lateinit var soundPool: SoundPool
    var soundsLoaded: Boolean = false

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

    var beepInAMajor: Int = 0

    fun configureSound(context: Context) {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .build()


        soundPool.setOnLoadCompleteListener(object : SoundPool.OnLoadCompleteListener {
            override fun onLoadComplete(
                soundPool: SoundPool, sampleId: Int,
                status: Int
            ) {
                soundsLoaded = true
            }
        })

        beepInAMajor = soundPool.load(context, R.raw.beep_a_major, 1)
    }

    fun play(activityNotificationEvent: ActivityNotificationEvent){

        if (SoundManager.soundsLoaded) {
            //TODO instead of directly playing the sound, perhaps we should insert it into a queue
            //TODO How long does a sample loop for? until something else happens? (could get annoying)

            beepInAMajor?.let{soundPool.play(
                activityNotificationEvent.soundSet.soundId,
                activityNotificationEvent.soundSet.leftVolume,
                activityNotificationEvent.soundSet.rightVolume,
                1,
                activityNotificationEvent.soundSet.loopCount,
                activityNotificationEvent.detectedActivity.confidence.toFloat()/100
            )}
        }
    }

/*    fun play(detectedActivity: DetectedActivity) {
        val volume = getCurrentVolume()

        if (soundLoaded) {
            //beepInAMajor?.let{soundPool.play(beepInAMajor as Int, volume, volume, 1, 0, 0.69f)}
            beepInAMajor?.let{
                soundPool.play(beepInAMajor as Int, volume, volume, 1, 0, detectedActivity.confidence.toFloat()/100)
            }
        }
    }*/

}
