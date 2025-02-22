package kreyer.my.util.vk_video.features.listvideo.data.datasource.api

import kreyer.my.util.vk_video.features.listvideo.data.datasource.api.retrofit.VideoApiService
import java.lang.Exception
import javax.inject.Inject
import kreyer.my.util.vk_video.core.datatype.Result
import kreyer.my.util.vk_video.features.listvideo.data.datasource.api.model.VideosApi

class VideoNetworkDataSource @Inject constructor(
    private val videoApiService: VideoApiService
) {

    suspend fun getListOfVideos(perPage: Int): Result<VideosApi> {
        return try {
            val listOfVideos = videoApiService.getListOfVideos(perPage)
            Result.Success(listOfVideos)
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }
}