package ru.netology.neworkapp.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
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
                userViewModel.getUsersIds(post.likeOwnerIds)
                if (post.likeOwnerIds.isEmpty()) {
                    Toast.makeText(context, R.string.no_likers, Toast.LENGTH_SHORT)
                        .show()
                } else findNavController().navigate(R.id.nav_bottom_sheet_fragment)
            }

            override fun onOpenMentions(post: Post) {
                userViewModel.getUsersIds(post.mentionIds)
                if (post.mentionIds.isEmpty()) {
                    Toast.makeText(context, R.string.no_mentions, Toast.LENGTH_SHORT)
                        .show()
                } else findNavController().navigate(R.id.nav_bottom_sheet_fragment)
            }

            override fun onPlayAudio(post: Post) {
                try {
                    val uri = Uri.parse(post.attachment?.url)
                    val intent = Intent(Intent.ACTION_VIEW)

                    intent.setDataAndType(uri, "audio/*")
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.no_play, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onPlayVideo(post: Post) {
                try {
                    val uri = Uri.parse(post.attachment?.url)
                    val intent = Intent(Intent.ACTION_VIEW)

                    intent.setDataAndType(uri, "video/*")
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, R.string.no_play, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })

        val id = parentFragment?.arguments?.getLong("id")

        binding.recyclerViewContainerFragmentPosts.adapter = adapter

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

        postViewModel.dataState.observe(viewLifecycleOwner) {
            when {
                it.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            binding.progressBarFragmentPosts.isVisible = it.loading
        }

        binding.swipeRefreshFragmentPosts.setOnRefreshListener(adapter::refresh)

        return binding.root
    }
}