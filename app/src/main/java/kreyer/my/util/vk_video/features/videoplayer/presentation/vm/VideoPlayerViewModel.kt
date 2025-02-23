package kreyer.my.util.vk_video.features.videoplayer.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kreyer.my.util.vk_video.features.videoplayer.presentation.model.VideoPlayerState
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val player: Player
) : ViewModel() {

    fun getPlayer() = player

    private val _playerState = MutableStateFlow(VideoPlayerState())
    val playerState: StateFlow<VideoPlayerState> = _playerState

    fun initializePlayer(videoUrl: String) {
        player.apply {
            setMediaItem(MediaItem.fromUri(videoUrl))
            prepare()
            play()

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    _playerState.update {
                        it.copy(
                            isPlaying = isPlaying,
                            isLoading = playbackState == Player.STATE_BUFFERING,
                            duration = duration,
                            position = currentPosition
                        )
                    }
                }
            })
        }
    }

    fun togglePlayback() {
        if (player.isPlaying) player.pause() else player.play()
    }

    fun seekForward() = player.seekForward()
//    fun seekBackward() = player.seekBackward()

    fun releasePlayer() {
        player.release()
    }
}