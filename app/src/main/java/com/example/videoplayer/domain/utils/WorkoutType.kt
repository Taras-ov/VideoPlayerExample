package com.example.videoplayer.domain.utils

enum class WorkoutType {
    TRAINING,
    LIVE,
    COMPLEX
}

fun Int.toWorkoutType(): WorkoutType =
    when (this) {
        1 -> WorkoutType.TRAINING
        2 -> WorkoutType.LIVE
        3 -> WorkoutType.COMPLEX
        else -> WorkoutType.TRAINING
    }