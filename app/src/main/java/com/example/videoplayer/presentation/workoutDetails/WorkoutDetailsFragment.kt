package com.example.videoplayer.presentation.workoutDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
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
    private var playbackPosition = 0L
    private var playWhenReady = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPlayer()
        setupClicks()
        observeState()

        viewModel.load(requireArguments().getInt("id"))
    }

    private fun setupClicks() {

        binding.fullscreenButton.setOnClickListener {
            viewModel.setFullscreen(!viewModel.state.value.isFullscreen)
        }
    }

    private fun setupPlayer() {

        player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->

            binding.playerView.player = exoPlayer

            exoPlayer.addListener(object : Player.Listener {

                override fun onPlaybackStateChanged(state: Int) {

                    binding.playerLoading.isVisible =
                        state == Player.STATE_BUFFERING
                }
            })
        }
    }

    private fun observeState() {

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                viewModel.state.collect(::render)
            }
        }
    }

    private fun render(state: WorkoutDetailsUiState) {

        binding.loadingContainer.isVisible = state.isLoading

        binding.errorView.isVisible = state.error != null

        binding.contentContainer.isVisible =
            !state.isLoading &&
                    state.error == null &&
                    !state.isFullscreen

        binding.playerView.isVisible =
            !state.isLoading &&
                    state.error == null

        if (state.error != null) {
            binding.errorView.text = state.error.localizedMessage
            return
        }

        state.workout?.let {

            binding.title.text = it.title

            binding.description.text =
                it.description.orEmpty()

            binding.meta.text =
                "${it.type} • ${it.duration}"
        }

        if (state.videoUrl != null && state.videoUrl != currentUrl) {

            currentUrl = state.videoUrl

            play(state.videoUrl)
        }

        if (state.isFullscreen) {
            enterFullscreen()
        } else {
            exitFullscreen()
        }
    }

    private fun play(url: String) {

        player?.apply {

            setMediaItem(MediaItem.fromUri(url))

            prepare()

            seekTo(playbackPosition)

            this.playWhenReady =
                this@WorkoutDetailsFragment.playWhenReady
        }
    }

    private fun enterFullscreen() {

        WindowCompat.setDecorFitsSystemWindows(
            requireActivity().window,
            false
        )

        WindowInsetsControllerCompat(
            requireActivity().window,
            binding.root
        ).hide(WindowInsetsCompat.Type.systemBars())

        binding.playerView.updateLayoutParams<ConstraintLayout.LayoutParams> {

            width = ConstraintLayout.LayoutParams.MATCH_PARENT
            height = ConstraintLayout.LayoutParams.MATCH_PARENT

            dimensionRatio = null
        }
    }

    private fun exitFullscreen() {

        WindowCompat.setDecorFitsSystemWindows(
            requireActivity().window,
            true
        )

        WindowInsetsControllerCompat(
            requireActivity().window,
            binding.root
        ).show(WindowInsetsCompat.Type.systemBars())

        binding.playerView.updateLayoutParams<ConstraintLayout.LayoutParams> {

            width = ConstraintLayout.LayoutParams.MATCH_PARENT
            height = 0

            dimensionRatio = "16:9"
        }
    }

    override fun onStart() {
        super.onStart()
        player?.playWhenReady = playWhenReady
    }

    override fun onStop() {

        playbackPosition =
            player?.currentPosition ?: 0

        playWhenReady =
            player?.playWhenReady ?: true

        player?.pause()

        super.onStop()
    }

    override fun onDestroyView() {

        playbackPosition =
            player?.currentPosition ?: 0

        playWhenReady =
            player?.playWhenReady ?: true

        player?.release()
        player = null

        _binding = null

        super.onDestroyView()
    }
}