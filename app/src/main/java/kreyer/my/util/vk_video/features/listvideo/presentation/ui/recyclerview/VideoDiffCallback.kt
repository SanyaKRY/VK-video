package kreyer.my.util.vk_video.features.listvideo.presentation.ui.recyclerview

import androidx.recyclerview.widget.DiffUtil
import kreyer.my.util.vk_video.features.listvideo.presentation.model.VideoUi

class VideoDiffCallback : DiffUtil.ItemCallback<VideoUi>() {

    override fun areItemsTheSame(oldItem: VideoUi, newItem: VideoUi): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: VideoUi, newItem: VideoUi): Boolean {
        return oldItem == newItem
    }
}