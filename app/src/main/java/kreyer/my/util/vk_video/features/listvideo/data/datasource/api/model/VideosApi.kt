package kreyer.my.util.vk_video.features.listvideo.data.datasource.api.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideosApi(
    @Json(name = "videos") val videos: List<VideoApi>
)

@JsonClass(generateAdapter = true)
data class VideoApi(
    @Json(name = "id") val videoId: Int,
    @Json(name = "duration") val videoDuration: Int,
    @Json(name = "url") val videoUrl: String,
    @Json(name = "image") val videoImage: String,
    @Json(name = "video_files") val videoFiles: List<VideoFile>
)

@JsonClass(generateAdapter = true)
data class VideoFile(
    @Json(name = "link") val link: String
)