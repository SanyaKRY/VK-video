package kreyer.my.util.vk_video.features.listvideo.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoUi(
    val videoId: Int,
    val videoDuration: Int,
    val videoUrl: String,
    val videoName: String,
    val videoImage: String
): Parcelable
