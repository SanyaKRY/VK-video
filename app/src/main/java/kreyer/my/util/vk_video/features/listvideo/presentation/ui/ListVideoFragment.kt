package kreyer.my.util.vk_video.features.listvideo.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kreyer.my.util.vk_video.databinding.FragmentListVideoBinding
import kreyer.my.util.vk_video.features.di.AdapterModule
import kreyer.my.util.vk_video.features.listvideo.presentation.event.ReloadGetListOfVideos
import kreyer.my.util.vk_video.features.listvideo.presentation.model.VideoUi
import kreyer.my.util.vk_video.features.listvideo.presentation.ui.recyclerview.VideoAdapter
import kreyer.my.util.vk_video.features.listvideo.presentation.vm.ListVideoViewModel
import javax.inject.Inject

@AndroidEntryPoint
class ListVideoFragment : Fragment() {

    private val viewModel: ListVideoViewModel by viewModels()

    private var _binding: FragmentListVideoBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var customAdapterFactory: AdapterModule
    private val videoClickListener: (
        videoUi: VideoUi
    ) -> Unit = {
            videoUi ->
        val action = ListVideoFragmentDirections
            .actionListVideoFragmentToVideoPlayerFragment(videoUi)
        findNavController().navigate(action)
    }

    private lateinit var videoAdapter: VideoAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListVideoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
        observerFlow()
        observerButton()
    }

    private fun setUpRecyclerView() {
        videoAdapter = customAdapterFactory.createFilmAdapter(videoClickListener)
        recyclerView = binding.recyclerView
        recyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observerFlow() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.stateFlow.collect { result ->
                    binding.spinner.root.isVisible = result.isLoading
                    videoAdapter.submitList(result.listOfVideos)
                    binding.errorDialog.root.isVisible = result.error != null
                }
            }
        }
    }

    private fun observerButton() {
        binding.errorDialog.reloadButton.setOnClickListener {
            viewModel.handleIntent(ReloadGetListOfVideos())
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            viewModel.handleIntent(ReloadGetListOfVideos())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}