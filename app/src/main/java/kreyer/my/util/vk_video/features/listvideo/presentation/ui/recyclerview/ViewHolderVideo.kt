package kreyer.my.util.vk_video.features.listvideo.presentation.ui.recyclerview

import androidx.recyclerview.widget.RecyclerView
import kreyer.my.util.vk_video.databinding.VideoItemBinding
import kreyer.my.util.vk_video.features.listvideo.presentation.model.VideoUi
import kreyer.my.util.vk_video.util.extensions.loadImage

class ViewHolderVideo(private val binding: VideoItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(videoUi: VideoUi) {
        binding.videoImage.loadImage(videoUi.videoImage)
        binding.videoName.text = videoUi.videoName
        binding.videoDuration.text = formatDuration(videoUi.videoDuration)
    }

    private fun formatDuration(duration: Int): String {
        val minutes = duration / 60
        val seconds = duration % 60
        return "$minutes:$seconds"
    }
}