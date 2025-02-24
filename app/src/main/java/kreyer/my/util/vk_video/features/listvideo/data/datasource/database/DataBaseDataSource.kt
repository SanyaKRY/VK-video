package kreyer.my.util.vk_video.features.listvideo.data.datasource.database

import kreyer.my.util.vk_video.features.listvideo.data.datasource.database.dao.VideoDao
import kreyer.my.util.vk_video.features.listvideo.data.datasource.database.model.VideoEntity
import javax.inject.Inject

class DataBaseDataSource @Inject constructor(
    private val videoDao: VideoDao
) {

    suspend fun insertVideosToDb(videos: List<VideoEntity>) {
        return videoDao.insertVideos(videos)
    }

    suspend fun getListOfVideosFromDb(): List<VideoEntity> {
        return videoDao.getCachedVideos()
    }

    suspend fun clearCacheVideosFromDb() {
        return videoDao.clearCache()
    }
}