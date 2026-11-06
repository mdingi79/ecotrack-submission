package com.ecotrack.app

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import com.ecotrack.app.ui.navigation.EcoTrackNavHost
import com.ecotrack.app.ui.theme.EcoTrackTheme
import com.ecotrack.app.viewmodel.ViewModelFactory
import kotlinx.coroutines.runBlocking

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        Log.i(TAG, "EcoTrack starting up")

        val app = application as EcoTrackApp
        val factory = ViewModelFactory(app)

        // Checked synchronously and cheaply (DataStore read) before the first frame so the user
        // never flashes through the login screen if they already have a valid session.
        val startLoggedIn = runBlocking { !app.sessionManager.getTokenOnce().isNullOrBlank() }

        setContent {
            val darkTheme by app.sessionManager.darkThemeFlow.collectAsState(initial = false)

            EcoTrackTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    EcoTrackNavHost(factory = factory, startLoggedIn = startLoggedIn)
                }
            }
        }
    }
}

private fun MainActivity.requestNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), 1001)
        }
    }
}
