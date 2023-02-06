package ru.netology.neworkapp.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.neworkapp.adapter.OnPostInteractionListener
import ru.netology.neworkapp.adapter.PostLoadingStateAdapter
import ru.netology.neworkapp.adapter.PostsAdapter
import ru.netology.neworkapp.databinding.FragmentPostsBinding
import ru.netology.neworkapp.dto.Post
import ru.netology.neworkapp.viewmodel.AuthViewModel
import ru.netology.neworkapp.viewmodel.PostViewModel
import ru.netology.neworkapp.viewmodel.WallViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WallFragment : Fragment() {

    private val postViewModel by viewModels<PostViewModel>()
    private val wallViewModel by viewModels<WallViewModel>()
    private val authViewModel by viewModels<AuthViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentPostsBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostsAdapter(object : OnPostInteractionListener {
            override fun onOpenPost(post: Post) {
                super.onOpenPost(post)
            }
        })

        val id = parentFragment?.arguments?.getLong("id")

        binding.recyclerViewContainerFragmentPosts.adapter =
            adapter.withLoadStateHeaderAndFooter(
                header = PostLoadingStateAdapter {
                    adapter.retry()
                },
                footer = PostLoadingStateAdapter {
                    adapter.retry()
                }
            )

        lifecycleScope.launchWhenCreated {
            if (id != null) {
                wallViewModel.loadUserWall(id).collectLatest(adapter::submitData)
            }
        }

        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest { state ->
                binding.swipeRefreshFragmentPosts.isRefreshing =
                    state.refresh is LoadState.Loading
            }
        }

        binding.swipeRefreshFragmentPosts.setOnRefreshListener(adapter::refresh)

        return binding.root
    }
}