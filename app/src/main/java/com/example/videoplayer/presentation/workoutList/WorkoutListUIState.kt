package com.example.videoplayer.presentation.workoutList

import com.example.videoplayer.domain.model.Workout
import com.example.videoplayer.domain.utils.WorkoutType

sealed interface WorkoutListUiState {

    data object Loading : WorkoutListUiState

    data class Content(
        val query: String = "",
        val selectedType: WorkoutType? = null,
        val workouts: List<Workout>
    ) : WorkoutListUiState

    data class Error(
        val throwable: Throwable
    ) : WorkoutListUiState
}