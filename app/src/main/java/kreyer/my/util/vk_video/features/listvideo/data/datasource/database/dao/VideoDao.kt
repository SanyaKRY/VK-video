package kreyer.my.util.vk_video.features.listvideo.data.datasource.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kreyer.my.util.vk_video.features.listvideo.data.datasource.database.model.VideoEntity

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<VideoEntity>)

    @Query("SELECT * FROM videos")
    suspend fun getCachedVideos(): List<VideoEntity>

    @Query("DELETE FROM videos")
    suspend fun clearCache()
}