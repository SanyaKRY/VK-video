package kreyer.my.util.vk_video.core.datatype

import java.lang.Exception

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val error: Exception) : Result<Nothing>()
    data object Loading : Result<Nothing>()
}