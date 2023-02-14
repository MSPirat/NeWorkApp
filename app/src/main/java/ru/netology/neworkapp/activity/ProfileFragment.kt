package ru.netology.neworkapp.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.neworkapp.R
import ru.netology.neworkapp.adapter.UserProfileAdapter
import ru.netology.neworkapp.databinding.FragmentProfileBinding
import ru.netology.neworkapp.dto.Post
import ru.netology.neworkapp.viewmodel.AuthViewModel
import ru.netology.neworkapp.viewmodel.PostViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val authViewModel by activityViewModels<AuthViewModel>()

    private val postViewModel by activityViewModels<PostViewModel>()

    private val profileTitles = arrayOf(
        R.string.title_posts,
        R.string.title_events,
        R.string.title_jobs
    )

    private var visibilityFabGroup = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentProfileBinding.inflate(
            inflater,
            container,
            false
        )

        val viewPagerProfile = binding.viewPagerFragmentProfile
        val tabLayoutProfile = binding.tabLayoutFragmentProfile
        val id = arguments?.getLong("id")
        val avatar = arguments?.getString("avatar")
        val name = arguments?.getString("name")

        viewPagerProfile.adapter = UserProfileAdapter(this)

        TabLayoutMediator(tabLayoutProfile, viewPagerProfile) { tab, position ->
            tab.text = getString(profileTitles[position])
        }.attach()

        with(binding) {
            textViewUserNameFragmentProfile.text = name
            Glide.with(imageViewUserAvatarFragmentProfile)
                .load("$avatar")
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_default_user_profile_image)
                .into(imageViewUserAvatarFragmentProfile)
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            if (authViewModel.authorized && id == it.id) {
                binding.fabAdd.visibility = View.VISIBLE
            }
        }

        binding.fabAdd.setOnClickListener {
            if (!visibilityFabGroup) {
                binding.fabAdd.setImageResource(R.drawable.ic_baseline_close_24)
                binding.fabGroup.visibility = View.VISIBLE
            } else {
                binding.fabAdd.setImageResource(R.drawable.ic_baseline_add_24)
                binding.fabGroup.visibility = View.GONE
            }
            visibilityFabGroup = !visibilityFabGroup
        }

        binding.fabAddPost.setOnClickListener {
            postViewModel.edit(Post.emptyPost)
            findNavController().navigate(R.id.action_nav_profile_to_new_post_fragment)
        }

        return binding.root
    }
}