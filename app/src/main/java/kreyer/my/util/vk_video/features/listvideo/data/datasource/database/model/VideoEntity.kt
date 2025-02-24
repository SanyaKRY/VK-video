package kreyer.my.util.vk_video.features.listvideo.data.datasource.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey
    val videoId: Int,
    val videoDuration: Int,
    val videoUrl: String,
    val videoName: String,
    val videoImage: String
)
