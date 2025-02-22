package kreyer.my.util.vk_video.features.listvideo.presentation.ui.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kreyer.my.util.vk_video.databinding.VideoItemBinding
import kreyer.my.util.vk_video.features.listvideo.presentation.model.VideoUi

class VideoAdapter @AssistedInject constructor(
    @Assisted("videoClickListener") private val videoClickListener: (
        videoUi: VideoUi
    ) -> Unit
) : ListAdapter<VideoUi, ViewHolderVideo>(
    VideoDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderVideo {
        val itemViewHolder = VideoItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        val viewHolder = ViewHolderVideo(itemViewHolder)
        setItemListener(viewHolder)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolderVideo, position: Int) {
        holder.apply {
            val current: VideoUi = getItem(position)
            bind(current)
        }
    }

    private fun setItemListener(viewHolderVideo: ViewHolderVideo) {
        viewHolderVideo.itemView.setOnClickListener {
            val position = viewHolderVideo.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                videoClickListener.invoke(getItem(position))
            }
        }
    }
}