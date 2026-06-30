package com.example.videoplayer.data.api

import com.example.videoplayer.data.api.dto.VideoWorkoutDto
import com.example.videoplayer.data.api.dto.WorkoutDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WorkoutApi {

    @GET("get_workouts")
    suspend fun getWorkouts(): List<WorkoutDto>

    @GET("get_video")
    suspend fun getVideo(
        @Query("id") id: Int
    ): VideoWorkoutDto
}