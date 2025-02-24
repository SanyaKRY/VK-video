package kreyer.my.util.vk_video.features.videoplayer.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kreyer.my.util.vk_video.features.videoplayer.presentation.model.PlayerIntent
import kreyer.my.util.vk_video.features.videoplayer.presentation.model.VideoPlayerState
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val exoPlayer: ExoPlayer
) : ViewModel() {

    private var updateJob: Job? = null
    private var timeoutJob: Job? = null

    private val _state = MutableStateFlow<VideoPlayerState>(VideoPlayerState.Loading)
    val state: StateFlow<VideoPlayerState> = _state.asStateFlow()

    private val intents = MutableSharedFlow<PlayerIntent>()

    init {
        handleIntents()
    }

    private fun handleIntents() {
        viewModelScope.launch {
            intents.collect { intent ->
                when (intent) {
                    is PlayerIntent.Initialize -> initializePlayer(intent.videoUrl)
                    PlayerIntent.PlayPauseToggle -> togglePlayback()
                    PlayerIntent.SeekForward -> seekForward()
                    PlayerIntent.SeekBackward -> seekBackward()
                }
            }
        }
    }

    fun processIntent(intent: PlayerIntent) {
        if (viewModelScope.isActive) {
            viewModelScope.launch {
                intents.emit(intent)
            }
        }
    }

    private fun initializePlayer(videoUrl: String) {
        try {
            if (exoPlayer.playbackState == Player.STATE_IDLE) {
                val mediaItem = MediaItem.fromUri(videoUrl)
                exoPlayer.setMediaItem(mediaItem)
                startTimeoutTimer()
                exoPlayer.prepare()
                exoPlayer.play()
            }

            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            timeoutJob?.cancel()
                            startPeriodicUpdate()
                            updateState()
                        }
                        Player.STATE_ENDED -> {
                            exoPlayer.seekTo(0)
                            updateState()
                        }
                        Player.STATE_BUFFERING -> {}
                        Player.STATE_IDLE -> {}
                    }
                }
            })
        } catch (e: Exception) {
            cancelTimeout()
            _state.value = VideoPlayerState.Error(e.message ?: "Unknown error")
        }
    }

    val player: ExoPlayer
        get() = exoPlayer

    private fun startTimeoutTimer() {
        timeoutJob?.cancel()
        timeoutJob = viewModelScope.launch {
            delay(10_000L)
            if (_state.value is VideoPlayerState.Loading) {
                _state.value = VideoPlayerState.Error("Video loading timeout")
                exoPlayer.stop()
                exoPlayer.release()
            }
        }
    }

    private fun startPeriodicUpdate() {
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            while (true) {
                delay(1_000L)
                updateState()
            }
        }
    }

    private fun togglePlayback() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            exoPlayer.play()
        }
        updateState()
    }

    private fun seekForward() {
        exoPlayer.seekForward()
        updateState()
    }

    private fun seekBackward() {
        exoPlayer.seekBack()
        updateState()
    }

    private fun updateState() {
        _state.value = VideoPlayerState.Ready(
            isPlaying = exoPlayer.isPlaying,
            currentPosition = exoPlayer.currentPosition,
            totalDuration = exoPlayer.duration
        )
    }

    private fun cancelTimeout() {
        timeoutJob?.cancel()
        timeoutJob = null
    }

    override fun onCleared() {
        cancelTimeout()
        updateJob?.cancel()
        exoPlayer.stop()
        exoPlayer.release()
        super.onCleared()
    }
}