package kreyer.my.util.vk_video.features.videoplayer.presentation.ui

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.OptIn
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
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

        viewModel.getExoPlayer().stop()
        viewModel.processIntent(PlayerIntent.Initialize(args.video.videoUrl))

        setupPlayerView()
        setupControls()
        collectState()
        handleOrientationChanges()
    }

    private fun setupPlayerView() {
        binding.playerView.player = viewModel.getExoPlayer()
        binding.playerView.useController = false
    }

    private fun collectState() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                when (state) {
                    is VideoPlayerState.Loading -> {
                        binding.loadingIndicator.visibility = View.VISIBLE
                        binding.errorText.visibility = View.GONE
                    }
                    is VideoPlayerState.Ready -> {
                        binding.loadingIndicator.visibility = View.GONE
                        binding.errorText.visibility = View.GONE
                        updateUI(state)
                    }
                    is VideoPlayerState.Error -> {
                        binding.loadingIndicator.visibility = View.GONE
                        binding.errorText.visibility = View.VISIBLE
                        binding.errorText.text = state.message
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

    private fun showLoading() {
        // Показать индикатор загрузки
    }

    private fun showError(message: String) {
        // Показать ошибку
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

    // Сохраняем форматирование времени
    private fun formatTime(milliseconds: Long): String {
        val totalSeconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        viewModel.getExoPlayer().pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.playerView.player = null
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        _binding = null
    }
}