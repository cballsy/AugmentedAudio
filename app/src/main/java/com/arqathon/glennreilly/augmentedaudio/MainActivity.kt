package com.arqathon.glennreilly.augmentedaudio


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ListView
import com.arqathon.glennreilly.augmentedaudio.service.ActivityRecognitionService
import com.google.android.gms.location.ActivityRecognitionClient
import android.media.AudioAttributes
import android.media.SoundPool
import android.media.AudioManager


class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    //private var mContext: Context? = null
    private var mActivityRecognitionClient: ActivityRecognitionClient? = null
    private var mAdapter: ActivitiesAdapter? = null
    private val activityDetectionPendingIntent: PendingIntent
        get() {
            val intent = Intent(this, ActivityRecognitionService::class.java)
            return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        }

    private lateinit var soundPool: SoundPool

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureSound()

        val detectedActivitiesListView = findViewById<View>(R.id.activities_listview) as ListView

        val detectedActivities = ActivityRecognitionService.detectedActivitiesFromJson(
            PreferenceManager.getDefaultSharedPreferences(this).getString(
                DETECTED_ACTIVITY, ""
            )
        )

        mAdapter = ActivitiesAdapter(this, detectedActivities)
        detectedActivitiesListView.adapter = mAdapter
        mActivityRecognitionClient = ActivityRecognitionClient(this)
    }

    private var soundLoaded: Boolean = false


    private var soundId: Int? = null

    fun configureSound() {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setAudioAttributes(attributes)
            .build()

        soundId = soundPool.load(this, R.raw.beep_a_major, 1)

        soundPool.setOnLoadCompleteListener(object : SoundPool.OnLoadCompleteListener {
            override fun onLoadComplete(
                soundPool: SoundPool, sampleId: Int,
                status: Int
            ) {
                soundLoaded = true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
        updateDetectedActivitiesList()
    }

    override fun onPause() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    fun requestUpdatesHandler(view: View) {
        val task = mActivityRecognitionClient!!.requestActivityUpdates(
            3000, activityDetectionPendingIntent
        )
        task.addOnSuccessListener { updateDetectedActivitiesList() }
    }

    protected fun updateDetectedActivitiesList() {

        play()
        val detectedActivities = ActivityRecognitionService.detectedActivitiesFromJson(
            PreferenceManager.getDefaultSharedPreferences(this)
                .getString(DETECTED_ACTIVITY, "")
        )

        mAdapter!!.updateActivities(detectedActivities)
    }

    fun play() {

        val audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val actVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING).toFloat()
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING).toFloat()
        val volume = actVolume / maxVolume


        if (soundLoaded) {
            soundId?.let{soundPool.play(soundId as Int, volume, volume, 1, 0, 0.69f)}
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        if (s == DETECTED_ACTIVITY) {
            updateDetectedActivitiesList()
        }
    }

    companion object {
        val DETECTED_ACTIVITY = ".DETECTED_ACTIVITY"
    }
}

/*
class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, TextToSpeech.OnInitListener {

    var mTTS: TextToSpeech? = null
    private val ACT_CHECK_TTS_DATA = 1000
    private lateinit var activityRecognitionClient: ActivityRecognitionClient
    var mApiClient: GoogleApiClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mApiClient = GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build()

        (mApiClient as GoogleApiClient).connect()
        activityRecognitionClient = ActivityRecognitionClient(this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            if (mTTS != null) {
                val result = (mTTS as TextToSpeech).setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA ||
                        result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS language is not supported", Toast.LENGTH_LONG).show()
                } else {
                    saySomething("TTS is ready", 0)
                }
            }
        } else {
            Toast.makeText(this, "TTS initialization failed",
                    Toast.LENGTH_LONG).show()
        }
    }

    private fun saySomething(text: String, qmode: Int) {
        if (qmode == 1)
            mTTS?.speak(text, TextToSpeech.QUEUE_ADD, null, null)
        else
            mTTS?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onConnected(bundle: Bundle?) {
        val intent = Intent(this, ActivityRecognizedService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val task = activityRecognitionClient.requestActivityUpdates(3000, pendingIntent)
        //task.addOnSuccessListener { }
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ACT_CHECK_TTS_DATA) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // Data exists, so we instantiate the TTS engine
                mTTS = TextToSpeech(this, this)
            } else {
                // Data is missing, so we start the TTS
                // installation process
                val installIntent = Intent()
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA)
                startActivity(installIntent)
            }
        }
    }

    override fun onDestroy() {
        if (mTTS != null) {
            (mTTS as TextToSpeech).apply {
                stop()
                shutdown()
            }
        }
        super.onDestroy()
    }
}
*/
