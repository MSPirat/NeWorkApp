package ru.netology.neworkapp.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.neworkapp.R
import ru.netology.neworkapp.adapter.UserProfileAdapter
import ru.netology.neworkapp.databinding.FragmentProfileBinding
import ru.netology.neworkapp.viewmodel.AuthViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private val authViewModel by viewModels<AuthViewModel>()

    private val profileTitles = arrayOf(
        R.string.title_posts,
        R.string.title_events,
        R.string.title_jobs
    )

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

        viewPagerProfile.adapter = UserProfileAdapter(this)

        TabLayoutMediator(tabLayoutProfile, viewPagerProfile) { tab, position ->
            tab.text = getString(profileTitles[position])
        }.attach()

        authViewModel.data.observe(viewLifecycleOwner) {
            if (!authViewModel.authorized)
                viewPagerProfile.adapter = UserProfileAdapter(this)
        }
        return binding.root
    }
}