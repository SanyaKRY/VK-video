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
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kreyer.my.util.vk_video.databinding.FragmentVideoPlayerBinding
import kreyer.my.util.vk_video.features.videoplayer.presentation.vm.VideoPlayerViewModel

@AndroidEntryPoint
class VideoPlayerFragment : Fragment() {

    private val viewModel: VideoPlayerViewModel by viewModels()

    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!

    private val args: VideoPlayerFragmentArgs by navArgs()

    private lateinit var exoPlayer: ExoPlayer

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
        viewModel.setVideo(args.video)

        setupPlayer()
        setupObservers()
        setupControls()
    }

    private fun setupPlayer() {
        exoPlayer = ExoPlayer.Builder(requireContext()).build().apply {
            binding.playerView.player = this

            // Слушатель для обновления времени в TextView
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    updateTimeTexts()
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    updateTimeTexts()
                }
            })
        }

        // Видео загружается после получения данных из viewModel
        viewModel.videoUrl.observe(viewLifecycleOwner) { videoUrl ->
            val mediaItem = MediaItem.fromUri(videoUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }
    }

    private fun updateTimeTexts() {
        val currentPosition = exoPlayer.currentPosition
        val totalDuration = exoPlayer.duration

        // Обновляем текстовые поля
        binding.currentTimeText.text = formatTime(currentPosition)
        if (totalDuration > 0) {
            binding.totalTimeText.text = formatTime(totalDuration)
        } else {
            binding.totalTimeText.text = "00:00" // Пустое значение, пока длительность неизвестна
        }
    }

    private fun formatTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / (1000 * 60)) % 60
        val hours = (milliseconds / (1000 * 60 * 60))

        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    private fun setupObservers() {
        // Слушаем изменения состояния плеера (например, воспроизведение, пауза и позиция)
        viewModel.isPlaying.observe(viewLifecycleOwner) {
            if (it) exoPlayer.play() else exoPlayer.pause()
        }

        viewModel.seekPosition.observe(viewLifecycleOwner) {
            exoPlayer.seekTo(exoPlayer.currentPosition + it)
        }
    }

    private fun setupControls() {
        binding.buttonPlayPause.setOnClickListener {
            viewModel.togglePlayback()
        }

        binding.buttonForward.setOnClickListener {
            viewModel.seekForward()
        }

        binding.buttonRewind.setOnClickListener {
            viewModel.seekBackward()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
    }
}