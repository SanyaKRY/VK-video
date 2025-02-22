package kreyer.my.util.vk_video.features.listvideo.presentation.model

import java.lang.Exception

data class ListVideoScreenState(
    val listOfVideos: List<VideoUi> = emptyList(),
    val isLoading: Boolean = false,
    val error: Exception? = null
)
