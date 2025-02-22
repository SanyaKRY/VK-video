package kreyer.my.util.vk_video.features.di

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import kreyer.my.util.vk_video.features.listvideo.presentation.model.VideoUi
import kreyer.my.util.vk_video.features.listvideo.presentation.ui.recyclerview.VideoAdapter

@AssistedFactory
interface AdapterModule {

    fun createFilmAdapter(
        @Assisted("videoClickListener") videoClickListener: (videoUi: VideoUi) -> Unit,
    ): VideoAdapter
}