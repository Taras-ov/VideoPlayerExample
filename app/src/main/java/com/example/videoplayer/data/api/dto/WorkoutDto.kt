package com.example.videoplayer.data.api.dto

data class WorkoutDto(
    val id: Int,
    val title: String,
    val description: String?,
    val type: Int,
    val duration: String
)