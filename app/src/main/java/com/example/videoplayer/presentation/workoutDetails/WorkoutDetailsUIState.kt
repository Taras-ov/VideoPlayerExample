package com.example.videoplayer.presentation.workoutDetails

import com.example.videoplayer.domain.model.Workout

data class WorkoutDetailsUiState(
    val isLoading: Boolean = false,
    val workout: Workout? = null,
    val videoUrl: String? = null,
    val isBuffering: Boolean = false,
    val isFullscreen: Boolean = false,
    val quality: Quality = Quality.HIGH,
    val error: Throwable? = null
)

enum class Quality(val label: String) {
    LOW("360p"),
    MEDIUM("480p"),
    HIGH("720p")
}