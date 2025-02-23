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
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kreyer.my.util.vk_video.R
import kreyer.my.util.vk_video.databinding.FragmentVideoPlayerBinding
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
        setupPlayer()
        setupControls()
        observePlayerState()
        handleOrientationChanges()
    }

    @OptIn(UnstableApi::class)
    private fun setupPlayer() {
        binding.playerView.apply {
            player = viewModel.getPlayer()
            setShowNextButton(false)
            setShowPreviousButton(false)
            setShowShuffleButton(false)
        }
        viewModel.initializePlayer(args.video.videoUrl)
    }

    private fun setupControls() {
        binding.apply {
            btnPlayPause.setOnClickListener { viewModel.togglePlayback() }
            btnForward.setOnClickListener { viewModel.seekForward() }
//            btnRewind.setOnClickListener { viewModel.seekBackward() }
        }
    }

    private fun observePlayerState() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.playerState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun updateUI(state: VideoPlayerState) {
        binding.apply {
            btnPlayPause.setImageResource(
                if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
            progressBar.isVisible = state.isLoading
            tvDuration.text = formatDuration(state.duration)
            tvCurrentTime.text = formatDuration(state.position)
        }
    }

    private fun handleOrientationChanges() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
        binding.playerView.setFullscreenButtonClickListener { isFullscreen ->
            activity?.requestedOrientation = if (isFullscreen) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        adjustFullscreenMode(newConfig.orientation)
    }

    private fun adjustFullscreenMode(orientation: Int) {
        val isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE
        activity?.window?.apply {
            if (isLandscape) {
                addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        )
            } else {
                clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.releasePlayer()
        _binding = null
    }

    private fun formatDuration(millis: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
        return "%02d:%02d".format(minutes, seconds)
    }
}