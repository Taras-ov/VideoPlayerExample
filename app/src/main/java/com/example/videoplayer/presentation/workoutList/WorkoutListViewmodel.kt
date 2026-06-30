package com.example.videoplayer.presentation.workoutList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.videoplayer.domain.model.Workout
import com.example.videoplayer.domain.repository.WorkoutRepository
import com.example.videoplayer.domain.utils.WorkoutType
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    private val repository: WorkoutRepository
) : ViewModel() {

    private val _state =
        MutableStateFlow<WorkoutListUiState>(WorkoutListUiState.Loading)

    val state = _state.asStateFlow()

    private var allWorkouts: List<Workout> = emptyList()

    private var currentQuery: String = ""
    private var currentType: WorkoutType? = null

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            _state.value = WorkoutListUiState.Loading

            runCatching {
                repository.getWorkouts()
            }.onSuccess { list ->
                allWorkouts = list

                applyFilters()
            }.onFailure { error ->
                _state.value = WorkoutListUiState.Error(error)
            }
        }
    }

    fun onQueryChanged(query: String) {
        currentQuery = query
        applyFilters()
    }

    fun onTypeSelected(type: WorkoutType?) {
        currentType = type
        applyFilters()
    }

    private fun applyFilters() {
        val filtered = allWorkouts.filter { workout ->

            val matchesQuery =
                workout.title.contains(currentQuery, ignoreCase = true)

            val matchesType =
                currentType == null || workout.type == currentType

            matchesQuery && matchesType
        }

        _state.value = WorkoutListUiState.Content(
            query = currentQuery,
            selectedType = currentType,
            workouts = filtered
        )
    }
}