package kreyer.my.util.vk_video.features.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kreyer.my.util.vk_video.features.listvideo.data.repository.ListVideoRepositoryImpl
import kreyer.my.util.vk_video.features.listvideo.domain.ListVideoRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindListVideoRepository(
        mainScreenRepositoryImpl: ListVideoRepositoryImpl
    ): ListVideoRepository
}