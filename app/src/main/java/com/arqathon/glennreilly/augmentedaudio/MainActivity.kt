package com.arqathon.glennreilly.augmentedaudio

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.common.ConnectionResult
import android.support.annotation.NonNull
import android.support.annotation.Nullable
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.ActivityRecognition
import android.app.PendingIntent
import com.arqathon.glennreilly.augmentedaudio.service.ActivityRecognizedService
import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.*
import android.system.Os.shutdown


class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, TextToSpeech.OnInitListener {

    var mTTS: TextToSpeech? = null
    private val ACT_CHECK_TTS_DATA = 1000

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
            mTTS?.speak(text, TextToSpeech.QUEUE_ADD, null)
        else
            mTTS?.speak(text, TextToSpeech.QUEUE_FLUSH, null)
    }

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
    }

    override fun onConnected(bundle: Bundle?) {
        val intent = Intent(this, ActivityRecognizedService::class.java)
        val pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mApiClient, 3000, pendingIntent)
    }

    override fun onConnectionSuspended(i: Int) {

    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent) {
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
