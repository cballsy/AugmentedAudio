package com.arqathon.glennreilly.augmentedaudio

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}

/*
 User
    -opens app
    -selects from a list of toggles for each 'station' or 'layer' that the app supports.
        enabling a station also enables a refresh time slider for that item (and expiry?)

 User
    -hits 'play' to start the Layers.

 Each Layer
    -get the relevant sensor data it requires
    -creates audio config to present to the user
    -inserts this audio config into a queue, awaiting play
        (note: queue may be long and items might be outdated by the time their time to play arrives.
         Should support an expiry)
 */
