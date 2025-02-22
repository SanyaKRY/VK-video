package kreyer.my.util.vk_video.features.listvideo.data.datasource.api.retrofit

import kreyer.my.util.vk_video.features.listvideo.data.datasource.api.model.VideosApi
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

private const val API_KEY = "BVvqziuGbeg2OYMWpAt3dJNfyyNLC2C11xZDeDSU71unL8nptuj6rPpX"

interface VideoApiService {

    @Headers("Authorization: $API_KEY")
    @GET("/videos/popular")
    suspend fun getListOfVideos(
        @Query("per_page") perPage: Int
    ): VideosApi
}