package kreyer.my.util.vk_video.features.listvideo.data.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import kreyer.my.util.vk_video.features.listvideo.data.datasource.database.dao.VideoDao
import kreyer.my.util.vk_video.features.listvideo.data.datasource.database.model.VideoEntity

@Database(entities = arrayOf(VideoEntity::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun videoDao(): VideoDao
}