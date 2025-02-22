package kreyer.my.util.vk_video.features.listvideo.presentation.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kreyer.my.util.vk_video.features.listvideo.domain.model.VideoDomain
import kreyer.my.util.vk_video.features.listvideo.domain.usecase.GetListOfVideosUseCase
import kreyer.my.util.vk_video.features.listvideo.presentation.model.ListVideoScreenState
import javax.inject.Inject
import kreyer.my.util.vk_video.core.datatype.Result

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

    private fun getListOfVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            val result: Result<List<VideoDomain>> = getListOfVideosUseCase.execute(PER_PAGE)
            withContext(Dispatchers.Main) {
                when (result) {
                    is Result.Success -> {
                    }
                    is Result.Error -> {
                    }
                    Result.Loading -> {}
                }
            }
        }
    }

    companion object {
        const val PER_PAGE = 5
    }
}