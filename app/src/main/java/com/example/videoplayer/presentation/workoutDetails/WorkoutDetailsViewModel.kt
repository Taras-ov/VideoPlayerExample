package com.example.videoplayer.presentation.workoutDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoplayer.domain.model.VideoWorkout
import com.example.videoplayer.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class WorkoutDetailsViewModel @Inject constructor(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WorkoutDetailsUiState(isLoading = true))
    val state = _state.asStateFlow()

    private var baseVideo: VideoWorkout? = null

    fun load(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            runCatching {
                val workout = repository.getWorkoutById(id)
                val video = repository.getVideo(id)

                workout to video
            }.onSuccess { (workout, video) ->

                baseVideo = video

                _state.value = WorkoutDetailsUiState(
                    isLoading = false,
                    workout = workout,
                    videoUrl = video.link
                )

            }.onFailure {
                _state.value = WorkoutDetailsUiState(
                    isLoading = false,
                    error = it
                )
            }
        }
    }

    fun changeQuality(quality: Quality) {
        val base = baseVideo ?: return

        val url = when (quality) {
            Quality.LOW -> base.link
            Quality.MEDIUM -> base.link
            Quality.HIGH -> base.link
        }

        _state.update {
            it.copy(
                quality = quality,
                videoUrl = url
            )
        }
    }

    fun setFullscreen(enabled: Boolean) {
        _state.update { it.copy(isFullscreen = enabled) }
    }
}