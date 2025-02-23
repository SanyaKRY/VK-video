package kreyer.my.util.vk_video.features.videoplayer.presentation.model

sealed interface PlayerIntent {
    data object PlayPauseToggle : PlayerIntent
    data object SeekForward : PlayerIntent
    data object SeekBackward : PlayerIntent
    data class Initialize(val videoUrl: String) : PlayerIntent
}