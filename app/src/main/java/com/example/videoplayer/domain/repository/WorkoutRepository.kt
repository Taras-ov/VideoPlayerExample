package com.example.videoplayer.domain.repository

import com.example.videoplayer.domain.model.VideoWorkout
import com.example.videoplayer.domain.model.Workout

interface WorkoutRepository {

    suspend fun getWorkouts(): List<Workout>

    suspend fun getVideo(id: Int): VideoWorkout

    suspend fun getWorkoutById(id: Int): Workout
}