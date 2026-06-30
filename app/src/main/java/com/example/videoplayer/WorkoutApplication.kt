package com.example.videoplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WorkoutApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }
}