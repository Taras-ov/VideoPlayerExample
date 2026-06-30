package com.example.videoplayer.presentation.workoutList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.videoplayer.R
import com.example.videoplayer.databinding.FragmentWorkoutListBinding
import com.example.videoplayer.domain.model.Workout
import com.example.videoplayer.domain.utils.WorkoutType
import com.example.videoplayer.presentation.workoutDetails.WorkoutDetailsFragment
import com.example.videoplayer.presentation.workoutList.rv.WorkoutAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkoutListFragment : Fragment() {

    private val viewModel: WorkoutListViewModel by viewModels()

    private var _binding: FragmentWorkoutListBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy {
        WorkoutAdapter(::openWorkout)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        setupUi()
        observeState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUi() {

        binding.searchInput.addTextChangedListener {
            viewModel.onQueryChanged(it.toString())
        }

        binding.chipGroup.setOnCheckedStateChangeListener { _, checkedIds ->

            val type = when (checkedIds.firstOrNull()) {
                R.id.chipTraining -> WorkoutType.TRAINING
                R.id.chipLive -> WorkoutType.LIVE
                R.id.chipComplex -> WorkoutType.COMPLEX
                else -> null
            }

            viewModel.onTypeSelected(type)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    render(state)
                }
            }
        }
    }

    private fun render(state: WorkoutListUiState) {
        when (state) {

            WorkoutListUiState.Loading -> {
                showLoading()
            }

            is WorkoutListUiState.Error -> {
                showError(state.throwable)
            }

            is WorkoutListUiState.Content -> {
                showContent(state)
            }
        }
    }

    private fun showContent(state: WorkoutListUiState.Content) {
        binding.progress.isVisible = false
        binding.errorView.isVisible = false

        val isEmpty = state.workouts.isEmpty()

        binding.emptyView.isVisible = isEmpty
        binding.recyclerView.isVisible = !isEmpty

        if (binding.searchInput.text.toString() != state.query) {
            binding.searchInput.setText(state.query)
            binding.searchInput.setSelection(state.query.length)
        }

        when (state.selectedType) {
            null -> binding.chipAll.isChecked = true
            WorkoutType.TRAINING -> binding.chipTraining.isChecked = true
            WorkoutType.LIVE -> binding.chipLive.isChecked = true
            WorkoutType.COMPLEX -> binding.chipComplex.isChecked = true
        }

        adapter.submitList(state.workouts)
    }

    private fun openWorkout(workout: Workout) {

        parentFragmentManager.beginTransaction()
            .replace(
                R.id.container,
                WorkoutDetailsFragment().apply {
                    arguments = bundleOf("id" to workout.id)
                }
            )
            .addToBackStack(null)
            .commit()
    }

    private fun showLoading() {
        binding.progress.isVisible = true

        binding.recyclerView.isVisible = false
        binding.emptyView.isVisible = false
        binding.errorView.isVisible = false
    }

    private fun showError(throwable: Throwable) {
        binding.progress.isVisible = false
        binding.recyclerView.isVisible = false
        binding.emptyView.isVisible = false

        binding.errorView.isVisible = true

        binding.errorView.text =
            throwable.message ?: "Неизвестная ошибка"
    }
}