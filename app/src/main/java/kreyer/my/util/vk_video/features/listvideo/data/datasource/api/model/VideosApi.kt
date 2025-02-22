package kreyer.my.util.vk_video.features.listvideo.data.datasource.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideosApi(
    @Json(name = "videos") val videos: List<VideoApi>
)

@JsonClass(generateAdapter = true)
data class VideoApi(
    @Json(name = "id") val videoId: Int
)