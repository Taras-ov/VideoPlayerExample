package com.example.videoplayer.presentation.workoutDetails

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.videoplayer.databinding.FragmentWorkoutDetailsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkoutDetailsFragment : Fragment() {

    private val viewModel: WorkoutDetailsViewModel by viewModels()

    private var _binding: FragmentWorkoutDetailsBinding? = null
    private val binding get() = _binding!!

    private var player: ExoPlayer? = null
    private var currentUrl: String? = null

    private var isBuffering = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val id = requireArguments().getInt("id")

        setupPlayer()
        observeState()

        viewModel.load(id)

        binding.fullscreenBtn.setOnClickListener {
            val newState = !(viewModel.state.value.isFullscreen)
            viewModel.setFullscreen(newState)
        }

        binding.qualityBtn.setOnClickListener {
            showQualityMenu()
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

    private fun render(state: WorkoutDetailsUiState) {

        binding.progressBar.isVisible = state.isBuffering

        binding.title.text = state.workout?.title.orEmpty()
        binding.description.text = state.workout?.description.orEmpty()
        binding.meta.text =
            "${state.workout?.type} • ${state.workout?.duration}"

        // fullscreen
        if (state.isFullscreen) enterFullscreen() else exitFullscreen()

        // video update
        state.videoUrl?.let { url ->
            if (url != currentUrl) {
                currentUrl = url
                play(url)
            }
        }

        if (state.error != null) {
            binding.title.text = "Error: ${state.error.message}"
        }
    }

    private fun setupPlayer() {
        player = ExoPlayer.Builder(requireContext()).build()
        binding.playerView.player = player

        player?.addListener(object : Player.Listener {

            override fun onPlaybackStateChanged(state: Int) {
                when (state) {

                    Player.STATE_BUFFERING -> {
                        updateBuffering(true)
                    }

                    Player.STATE_READY -> {
                        updateBuffering(false)
                    }

                    Player.STATE_ENDED -> {
                        updateBuffering(false)
                    }

                    Player.STATE_IDLE -> Unit
                }
            }
        })
    }

    private fun updateBuffering(show: Boolean) {
        isBuffering = show
        binding.progressBar.isVisible = show
    }

    private fun play(url: String) {
        val item = MediaItem.fromUri(url)

        player?.apply {
            setMediaItem(item)
            prepare()
            playWhenReady = true
        }
    }

    private fun enterFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)

        binding.playerView.layoutParams.height =
            ViewGroup.LayoutParams.MATCH_PARENT
    }

    private fun exitFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, true)

        binding.playerView.layoutParams.height =
            resources.displayMetrics.density.times(220).toInt()
    }

    private fun showQualityMenu() {
        val qualities = arrayOf("LOW", "MEDIUM", "HIGH")

        AlertDialog.Builder(requireContext())
            .setItems(qualities) { _, which ->
                val q = Quality.values()[which]
                viewModel.changeQuality(q)
            }
            .show()
    }
}