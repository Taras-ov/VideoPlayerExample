package com.example.videoplayer.data.repository

import com.example.videoplayer.data.api.WorkoutApi
import com.example.videoplayer.data.api.dto.WorkoutDto
import com.example.videoplayer.data.mapper.toDomain
import com.example.videoplayer.domain.model.Workout
import com.example.videoplayer.domain.repository.WorkoutRepository
import jakarta.inject.Inject

class WorkoutRepositoryImpl @Inject constructor(
    private val api: WorkoutApi
) : WorkoutRepository {

    override suspend fun getWorkouts() =
        api.getWorkouts().map(WorkoutDto::toDomain)

    override suspend fun getVideo(id: Int) =
        api.getVideo(id).toDomain()

    override suspend fun getWorkoutById(id: Int): Workout {
        return api.getWorkouts()
            .map(WorkoutDto::toDomain)
            .first { it.id == id }
    }
}