package kreyer.my.util.vk_video.features.videoplayer.presentation.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kreyer.my.util.vk_video.R
import kreyer.my.util.vk_video.databinding.FragmentVideoPlayerBinding
import kreyer.my.util.vk_video.features.videoplayer.presentation.model.PlayerIntent
import kreyer.my.util.vk_video.features.videoplayer.presentation.model.VideoPlayerState
import kreyer.my.util.vk_video.features.videoplayer.presentation.vm.VideoPlayerViewModel
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class VideoPlayerFragment : Fragment() {

    private val viewModel: VideoPlayerViewModel by viewModels()

    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!

    private val args: VideoPlayerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.player.stop()
        viewModel.processIntent(PlayerIntent.Initialize(args.video.videoUrl))
        setupPlayerView()
        setupControls()
        collectState()
        handleOrientationChanges()
        setButton()
    }

    private fun setupPlayerView() {
        binding.playerView.player = viewModel.player
        binding.playerView.useController = false
    }

    private fun setButton() {
        binding.errorDialog.reloadButton.visibility = View.GONE
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is VideoPlayerState.Loading -> {
                            binding.spinner.root.isVisible = true
                            binding.playerView.visibility = View.GONE
                            binding.timeContainer.visibility = View.GONE
                            binding.controllerContainer.visibility = View.GONE
                        }
                        is VideoPlayerState.Ready -> {
                            binding.spinner.root.isVisible = false
                            updateUI(state)
                            binding.playerView.visibility = View.VISIBLE
                            binding.timeContainer.visibility = View.VISIBLE
                            binding.controllerContainer.visibility = View.VISIBLE
                        }
                        is VideoPlayerState.Error -> {
                            binding.spinner.root.isVisible = false
                            binding.errorDialog.root.isVisible = true
                            binding.playerView.visibility = View.GONE
                            binding.timeContainer.visibility = View.GONE
                            binding.controllerContainer.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    private fun updateUI(state: VideoPlayerState.Ready) {
        binding.apply {
            currentTimeText.text = formatTime(state.currentPosition)
            totalTimeText.text = formatTime(state.totalDuration)
            buttonPlayPause.setImageResource(
                if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
        }
    }

    private fun setupControls() {
        binding.apply {
            buttonPlayPause.setOnClickListener {
                viewModel.processIntent(PlayerIntent.PlayPauseToggle)
            }
            buttonForward.setOnClickListener {
                viewModel.processIntent(PlayerIntent.SeekForward)
            }
            buttonRewind.setOnClickListener {
                viewModel.processIntent(PlayerIntent.SeekBackward)
            }
        }
    }

    private fun handleOrientationChanges() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
    }

    private fun formatTime(milliseconds: Long): String {
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "$minutes:$seconds"
    }

    override fun onPause() {
        super.onPause()
        viewModel.player.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.playerView.player = null
        _binding = null
    }
}