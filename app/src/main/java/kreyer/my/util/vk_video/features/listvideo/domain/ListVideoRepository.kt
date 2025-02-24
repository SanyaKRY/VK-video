package kreyer.my.util.vk_video.features.listvideo.domain

import kreyer.my.util.vk_video.features.listvideo.domain.model.VideoDomain
import kreyer.my.util.vk_video.core.datatype.Result
import kreyer.my.util.vk_video.features.listvideo.data.datasource.database.model.VideoEntity

interface ListVideoRepository {

    suspend fun insertVideos(videos: List<VideoEntity>)

    suspend fun getListOfVideos(perPage: Int): Result<List<VideoDomain>>

    suspend fun getListOfVideosFromDb(): List<VideoEntity>

    suspend fun clearCacheVideosFromDb()
}