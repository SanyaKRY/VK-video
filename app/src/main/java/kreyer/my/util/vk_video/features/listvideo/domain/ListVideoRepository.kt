package kreyer.my.util.vk_video.features.listvideo.domain

import kreyer.my.util.vk_video.features.listvideo.domain.model.VideoDomain
import kreyer.my.util.vk_video.core.datatype.Result

interface ListVideoRepository {

    suspend fun getListOfVideos(perPage: Int): Result<List<VideoDomain>>
}