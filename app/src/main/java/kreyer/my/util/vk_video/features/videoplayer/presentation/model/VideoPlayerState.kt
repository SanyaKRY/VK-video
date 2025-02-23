package kreyer.my.util.vk_video.features.videoplayer.presentation.model

data class VideoPlayerState(
    val isPlaying: Boolean = false,
    val isLoading: Boolean = true,
    val duration: Long = 0L,
    val position: Long = 0L
)
