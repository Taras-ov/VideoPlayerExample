package com.example.videoplayer.domain.model

import com.example.videoplayer.domain.utils.WorkoutType

data class Workout(
    val id: Int,
    val title: String,
    val description: String,
    val type: WorkoutType,
    val duration: String
)