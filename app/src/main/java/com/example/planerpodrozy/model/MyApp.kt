package com.example.planerpodrozy

import android.app.Application
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        MapLibre.getInstance(
            this,
            null,
            WellKnownTileServer.MapLibre
        )
    }
}