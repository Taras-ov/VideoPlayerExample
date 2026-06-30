package com.example.videoplayer.presentation.workoutList.rv

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.videoplayer.databinding.ItemWorkoutBinding
import com.example.videoplayer.domain.model.Workout

class WorkoutAdapter(
    private val onWorkoutClick: (Workout) -> Unit
) : ListAdapter<Workout, WorkoutViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WorkoutViewHolder {

        val binding = ItemWorkoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return WorkoutViewHolder(
            binding = binding,
            onWorkoutClick = onWorkoutClick
        )
    }

    override fun onBindViewHolder(
        holder: WorkoutViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }
}

class WorkoutViewHolder(
    private val binding: ItemWorkoutBinding,
    private val onWorkoutClick: (Workout) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var workout: Workout? = null

    init {
        binding.root.setOnClickListener {
            workout?.let(onWorkoutClick)
        }
    }

    fun bind(item: Workout) {

        workout = item

        binding.title.text = item.title
        binding.duration.text = item.duration
        binding.type.text = item.type.name
        binding.description.text = item.description
    }
}

class DiffCallback : DiffUtil.ItemCallback<Workout>() {

    override fun areItemsTheSame(
        oldItem: Workout,
        newItem: Workout
    ): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Workout,
        newItem: Workout
    ): Boolean =
        oldItem == newItem
}