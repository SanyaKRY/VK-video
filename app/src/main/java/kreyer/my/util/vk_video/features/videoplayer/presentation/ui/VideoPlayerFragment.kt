package kreyer.my.util.vk_video.features.videoplayer.presentation.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import kreyer.my.util.vk_video.databinding.FragmentVideoPlayerBinding
import kreyer.my.util.vk_video.features.videoplayer.presentation.vm.VideoPlayerViewModel

@AndroidEntryPoint
class VideoPlayerFragment : Fragment() {

    private val viewModel: VideoPlayerViewModel by viewModels()

    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}