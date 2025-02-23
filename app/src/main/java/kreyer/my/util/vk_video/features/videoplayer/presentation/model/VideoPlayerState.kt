package kreyer.my.util.vk_video.features.videoplayer.presentation.model

sealed interface VideoPlayerState {
    data object Loading : VideoPlayerState
    data class Ready(
        val isPlaying: Boolean,
        val currentPosition: Long,
        val totalDuration: Long
    ) : VideoPlayerState
    data class Error(val message: String) : VideoPlayerState
}
