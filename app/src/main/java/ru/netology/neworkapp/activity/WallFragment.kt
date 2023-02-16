package ru.netology.neworkapp.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.neworkapp.R
import ru.netology.neworkapp.adapter.OnPostInteractionListener
import ru.netology.neworkapp.adapter.PostLoadingStateAdapter
import ru.netology.neworkapp.adapter.PostsAdapter
import ru.netology.neworkapp.databinding.FragmentPostsBinding
import ru.netology.neworkapp.dto.Post
import ru.netology.neworkapp.viewmodel.AuthViewModel
import ru.netology.neworkapp.viewmodel.PostViewModel
import ru.netology.neworkapp.viewmodel.UserViewModel
import ru.netology.neworkapp.viewmodel.WallViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WallFragment : Fragment() {

    private val postViewModel by activityViewModels<PostViewModel>()
    private val wallViewModel by activityViewModels<WallViewModel>()
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()

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

            override fun onOpenPost(post: Post) {}

            override fun onEditPost(post: Post) {
                postViewModel.edit(post)
                val bundle = Bundle().apply {
                    putString("content", post.content)
                }
                findNavController()
                    .navigate(R.id.nav_new_post_fragment, bundle)
            }

            override fun onRemovePost(post: Post) {
                postViewModel.removeById(post.id)
            }

            override fun onLikePost(post: Post) {
                if (authViewModel.authorized) {
                    if (!post.likedByMe)
                        postViewModel.likeById(post.id)
                    else postViewModel.unlikeById(post.id)
                } else {
                    Toast.makeText(activity, R.string.error_auth, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onMentionPost(post: Post) {
                if (authViewModel.authorized) {
                    postViewModel.edit(post)
                    val bundle = Bundle().apply {
                        putString("open", "mention")
                    }
                    findNavController().navigate(R.id.nav_users, bundle)
                } else {
                    Toast.makeText(activity, R.string.error_auth, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onSharePost(post: Post) {
                if (authViewModel.authorized) {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(intent, "Share post")
                    startActivity(shareIntent)
                } else {
                    Toast.makeText(activity, R.string.error_auth, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onOpenLikers(post: Post) {
                userViewModel.getLikeOwnerIds(post)
                findNavController().navigate(R.id.nav_bottom_sheet_fragment)
            }

            override fun onOpenMentions(post: Post) {
                userViewModel.getMentionIds(post)
                findNavController().navigate(R.id.nav_bottom_sheet_fragment)
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