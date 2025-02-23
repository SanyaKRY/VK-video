package kreyer.my.util.vk_video.features.listvideo.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kreyer.my.util.vk_video.features.listvideo.domain.model.VideoDomain
import kreyer.my.util.vk_video.features.listvideo.domain.usecase.GetListOfVideosUseCase
import kreyer.my.util.vk_video.features.listvideo.presentation.model.ListVideoScreenState
import javax.inject.Inject
import kreyer.my.util.vk_video.core.datatype.Result
import kreyer.my.util.vk_video.features.listvideo.presentation.event.ListVideoScreenEvent
import kreyer.my.util.vk_video.features.listvideo.presentation.event.ReloadGetListOfVideos
import kreyer.my.util.vk_video.features.listvideo.presentation.model.VideoUi

@HiltViewModel
class ListVideoViewModel @Inject constructor(
    private val getListOfVideosUseCase: GetListOfVideosUseCase
) : ViewModel() {

    private val _stateFlow: MutableStateFlow<ListVideoScreenState> =
        MutableStateFlow(ListVideoScreenState(isLoading = true))
    val stateFlow: Flow<ListVideoScreenState>
        get() = _stateFlow

    init {
        getListOfVideos()
    }

    fun handleIntent(event: ListVideoScreenEvent) {
        when (event) {
            is ReloadGetListOfVideos -> reloadGetListOfVideos()
        }
    }

    private fun getListOfVideos() {
            viewModelScope.launch(Dispatchers.IO) {
            delay(2_000)
            val result: Result<List<VideoDomain>> = getListOfVideosUseCase.execute(PER_PAGE)
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                        _stateFlow.value = _stateFlow.value
                            .copy(listOfVideos = result.data.map {
                                VideoUi(
                                    videoId = it.videoId,
                                    videoDuration = it.videoDuration,
                                    videoUrl = it.videoUrl,
                                    videoName = it.videoName,
                                    videoImage = it.videoImage
                                )
                            }, isLoading = false, error = null)
                    }
                    is Result.Error -> {
                        _stateFlow.value =
                            _stateFlow.value.copy(listOfVideos = emptyList(), isLoading = false, error = result.error)
                    }
                    Result.Loading -> {
                        _stateFlow.value =
                            _stateFlow.value.copy(listOfVideos = emptyList(), isLoading = true, error = null)
                    }
                }
            }
        }
    }

    private fun reloadGetListOfVideos() {
        _stateFlow.value =
            _stateFlow.value.copy(listOfVideos = emptyList(), isLoading = true, error = null)
        getListOfVideos()
    }

    companion object {
        const val PER_PAGE = 10
    }
}