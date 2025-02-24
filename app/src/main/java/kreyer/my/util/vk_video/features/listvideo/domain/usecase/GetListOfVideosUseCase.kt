package kreyer.my.util.vk_video.features.listvideo.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kreyer.my.util.vk_video.features.listvideo.domain.ListVideoRepository
import kreyer.my.util.vk_video.features.listvideo.domain.model.VideoDomain
import javax.inject.Inject
import kreyer.my.util.vk_video.core.datatype.Result
import kreyer.my.util.vk_video.features.listvideo.data.datasource.database.model.VideoEntity

class GetListOfVideosUseCase @Inject constructor(
    private val listVideoRepository: ListVideoRepository
) {

    fun execute(perPage: Int) : Flow<Result<List<VideoDomain>>> = flow {

        val listOfVideoFromDb: List<VideoEntity> = listVideoRepository.getListOfVideosFromDb()
        val listOfVideoFromDbDomain = listOfVideoFromDb.map {
            VideoDomain(
                videoId = it.videoId,
                videoDuration = it.videoDuration,
                videoUrl = it.videoUrl,
                videoName = it.videoName,
                videoImage = it.videoImage
            )
        }
        if (listOfVideoFromDb.isNotEmpty()) {
            emit(Result.Success(listOfVideoFromDbDomain))
        }

        val getVideoResult = listVideoRepository.getListOfVideos(perPage)
        when(getVideoResult) {
            is Result.Success -> {
                val listOfVideoForDbSave =getVideoResult.data.map {
                    VideoEntity(
                        videoId = it.videoId,
                        videoDuration = it.videoDuration,
                        videoUrl = it.videoUrl,
                        videoName = it.videoName,
                        videoImage = it.videoImage
                    )
                }
                listVideoRepository.clearCacheVideosFromDb()
                listVideoRepository.insertVideos(listOfVideoForDbSave)

                emit(Result.Success(getVideoResult.data))
            }
            is Result.Error -> {
                emit(Result.Error(getVideoResult.error))
            }
            Result.Loading -> {
                emit(Result.Loading)
            }
        }
    }
}