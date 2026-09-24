package com.ecotrack.app

import android.app.Application
import com.ecotrack.app.data.local.AppDatabase
import com.ecotrack.app.data.remote.RetrofitClient
import com.ecotrack.app.data.repository.AuthRepository
import com.ecotrack.app.data.repository.WasteRepository
import com.ecotrack.app.util.SessionManager

/**
 * No Hilt/Dagger for this prototype - a small manual container keeps the sample easy to read
 * for markers while still separating concerns (Repository pattern, per the Part 1 design doc).
 */
class EcoTrackApp : Application() {

    lateinit var sessionManager: SessionManager
        private set
    lateinit var authRepository: AuthRepository
        private set
    lateinit var wasteRepository: WasteRepository
        private set

    override fun onCreate() {
        super.onCreate()
        sessionManager = SessionManager(this)
        val api = RetrofitClient.create(sessionManager)
        val db = AppDatabase.getInstance(this)

        authRepository = AuthRepository(api, sessionManager)
        wasteRepository = WasteRepository(api, db.wasteCategoryDao(), db.wasteLogDao())
    }
}
