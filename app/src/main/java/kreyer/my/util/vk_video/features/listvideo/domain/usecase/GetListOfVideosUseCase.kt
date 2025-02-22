package kreyer.my.util.vk_video.features.listvideo.domain.usecase

import kreyer.my.util.vk_video.features.listvideo.domain.ListVideoRepository
import kreyer.my.util.vk_video.features.listvideo.domain.model.VideoDomain
import javax.inject.Inject
import kreyer.my.util.vk_video.core.datatype.Result

class GetListOfVideosUseCase @Inject constructor(
    private val listVideoRepository: ListVideoRepository
) {

    suspend fun execute(perPage: Int) : Result<List<VideoDomain>> {
        return listVideoRepository.getListOfVideos(perPage)
    }
}