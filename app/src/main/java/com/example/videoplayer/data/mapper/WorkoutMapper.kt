package com.example.videoplayer.data.mapper

import com.example.videoplayer.data.api.dto.VideoWorkoutDto
import com.example.videoplayer.data.api.dto.WorkoutDto
import com.example.videoplayer.domain.model.VideoWorkout
import com.example.videoplayer.domain.model.Workout
import com.example.videoplayer.domain.utils.toWorkoutType

internal fun WorkoutDto.toDomain() = Workout(
    id = id,
    title = title,
    description = description.orEmpty(),
    type = type.toWorkoutType(),
    duration = duration
)

internal fun VideoWorkoutDto.toDomain() = VideoWorkout(
    id = id,
    duration = duration,
    link = link
)