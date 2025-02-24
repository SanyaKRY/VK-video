package kreyer.my.util.vk_video

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kreyer.my.util.vk_video.features.listvideo.domain.usecase.GetListOfVideosUseCase
import kreyer.my.util.vk_video.features.listvideo.presentation.vm.ListVideoViewModel
import kreyer.my.util.vk_video.core.datatype.Result
import kreyer.my.util.vk_video.features.listvideo.domain.model.VideoDomain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class ListVideoViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: ListVideoViewModel
    private val mockUseCase: GetListOfVideosUseCase = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Test get success result`() = runTest {
        val testVideos = listOf(
            VideoDomain(
                videoId = 1,
                videoDuration = 1,
                videoUrl = "url1",
                videoName = "video1",
                videoImage = "image1"
            )
        )
        coEvery { mockUseCase.execute(any()) } returns Result.Success(testVideos)
        viewModel = ListVideoViewModel(mockUseCase)
        viewModel.stateFlow.test {
            assertTrue(awaitItem().isLoading)
            advanceTimeBy(2000)
            val successState = awaitItem()
            assertFalse(successState.isLoading)
            assertEquals(1, successState.listOfVideos.size)
            assertEquals(1, successState.listOfVideos[0].videoId)
            assertEquals("video1", successState.listOfVideos[0].videoName)
            assertNull(successState.error)
            cancel()
        }
    }

    @Test
    fun `Test get error result`() = runTest {
        val testError = IOException("Network error")
        coEvery { mockUseCase.execute(any()) } returns Result.Error(testError)
        viewModel = ListVideoViewModel(mockUseCase)
        viewModel.stateFlow.test {
            assertTrue(awaitItem().isLoading)
            val errorState = awaitItem()
            assertFalse(errorState.isLoading)
            assertTrue(errorState.listOfVideos.isEmpty())
            assertEquals(testError, errorState.error)
            cancel()
        }
    }

    @Test
    fun `Test get loading result`() = runTest {
        coEvery { mockUseCase.execute(any()) } returns Result.Loading
        viewModel = ListVideoViewModel(mockUseCase)
        viewModel.stateFlow.test {
            val initialState = awaitItem()
            assertTrue(initialState.isLoading)
            cancel()
        }
    }

    @Test
    fun `Test map VideoDomain to VideoUi`() = runTest {
        val testDomain = VideoDomain(
            videoId = 123,
            videoDuration = 123,
            videoUrl = "domain-url",
            videoName = "domain-video",
            videoImage = "domain-image"
        )
        coEvery { mockUseCase.execute(any()) } returns Result.Success(listOf(testDomain))
        viewModel = ListVideoViewModel(mockUseCase)
        viewModel.stateFlow.test {
            awaitItem()
            val successState = awaitItem()
            val uiModel = successState.listOfVideos.first()
            assertEquals(testDomain.videoId, uiModel.videoId)
            assertEquals(testDomain.videoDuration, uiModel.videoDuration)
            assertEquals(testDomain.videoUrl, uiModel.videoUrl)
            assertEquals(testDomain.videoName, uiModel.videoName)
            assertEquals(testDomain.videoImage, uiModel.videoImage)
            cancel()
        }
    }
}