package kreyer.my.util.vk_video.features.videoplayer.presentation.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kreyer.my.util.vk_video.features.listvideo.presentation.model.VideoUi
import kreyer.my.util.vk_video.features.videoplayer.presentation.model.VideoPlayerState
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
) : ViewModel() {

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> = _isPlaying

    private val _seekPosition = MutableLiveData<Long>()
    val seekPosition: LiveData<Long> = _seekPosition

    private val _videoUrl = MutableLiveData<String>()
    val videoUrl: LiveData<String> = _videoUrl

    fun setVideo(videoUi: VideoUi) {
        _videoUrl.postValue(videoUi.videoUrl)
        _isPlaying.postValue(true)
    }

    fun togglePlayback() {
        _isPlaying.value = !(isPlaying.value ?: true)
    }

    fun seekForward() {
        _seekPosition.postValue(10_000L) // Перемотка на 10 секунд вперед
    }

    fun seekBackward() {
        _seekPosition.postValue(-10_000L) // Перемотка на 10 секунд назад
    }
}