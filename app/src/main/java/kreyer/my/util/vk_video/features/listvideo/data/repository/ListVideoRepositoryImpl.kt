package kreyer.my.util.vk_video.features.listvideo.data.repository

import kreyer.my.util.vk_video.features.listvideo.data.datasource.api.VideoNetworkDataSource
import kreyer.my.util.vk_video.features.listvideo.data.datasource.api.model.VideosApi
import kreyer.my.util.vk_video.features.listvideo.domain.ListVideoRepository
import kreyer.my.util.vk_video.features.listvideo.domain.model.VideoDomain
import javax.inject.Inject
import kreyer.my.util.vk_video.core.datatype.Result

class ListVideoRepositoryImpl @Inject constructor(
    private val videoNetworkDataSource: VideoNetworkDataSource
) : ListVideoRepository {

    override suspend fun getListOfVideos(perPage: Int): Result<List<VideoDomain>> {
        return when (val result: Result<VideosApi> = videoNetworkDataSource.getListOfVideos(perPage)) {
            is Result.Success ->  {
                Result.Success(
                    result.data.videos.map {
                        VideoDomain(
                            videoId = it.videoId,
                            videoDuration = it.videoDuration,
                            videoUrl = it.videoFiles.first().link,
                            videoName = it.videoUrl
                                .split("/video/")[1]
                                    .substringBeforeLast("-")
                                    .replace("-", " ")
                                    .replaceFirstChar { it.uppercase() },
                            videoImage = it.videoImage
                        )
                    }
                )
            }
            is Result.Error -> Result.Error(result.error)
            is Result.Loading -> Result.Loading
        }
    }
}