package kreyer.my.util.vk_video.features.videoplayer.presentation.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kreyer.my.util.vk_video.features.listvideo.presentation.model.VideoUi
import kreyer.my.util.vk_video.features.videoplayer.presentation.model.PlayerIntent
import kreyer.my.util.vk_video.features.videoplayer.presentation.model.VideoPlayerState
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val exoPlayer: ExoPlayer
) : ViewModel() {

    private var updateJob: Job? = null

    fun getExoPlayer() = exoPlayer

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
                exoPlayer.prepare()
                exoPlayer.play()
            }

            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> {
                            startPeriodicUpdate() // Запускаем обновление при готовности
                            updateState()
                        }
                        Player.STATE_ENDED -> {
                            exoPlayer.seekTo(0)
                            updateState()
                        }
                    }
                }
            })
        } catch (e: Exception) {
            _state.value = VideoPlayerState.Error(e.message ?: "Unknown error")
        }
    }

    private fun startPeriodicUpdate() {
        updateJob?.cancel() // Отменяем предыдущую корутину
        updateJob = viewModelScope.launch {
            while (true) {
                delay(1000L) // Обновляем каждую секунду
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

    override fun onCleared() {
        updateJob?.cancel() // Отменяем корутину при очистке
        exoPlayer.stop()
        exoPlayer.release()
        super.onCleared()
    }
}