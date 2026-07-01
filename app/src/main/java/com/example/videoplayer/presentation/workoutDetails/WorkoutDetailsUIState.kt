package com.example.videoplayer.presentation.workoutDetails

import com.example.videoplayer.domain.model.Workout

data class WorkoutDetailsUiState(
    val isLoading: Boolean = true,
    val isBuffering: Boolean = false,
    val workout: Workout? = null,
    val videoUrl: String? = null,
    val isFullscreen: Boolean = false,
    val error: Throwable? = null
)