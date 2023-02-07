package ru.netology.neworkapp.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.neworkapp.R
import ru.netology.neworkapp.adapter.UserAdapter
import ru.netology.neworkapp.adapter.UserCallback
import ru.netology.neworkapp.databinding.FragmentUsersBinding
import ru.netology.neworkapp.dto.User
import ru.netology.neworkapp.viewmodel.PostViewModel
import ru.netology.neworkapp.viewmodel.UserViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class UsersFragment : Fragment() {

    private val userViewModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentUsersBinding.inflate(
            inflater,
            container,
            false
        )

        userViewModel.getUsers()

        val adapter = UserAdapter(object : UserCallback {
            override fun openProfile(user: User) {
                val bundle = Bundle().apply {
                    putLong("id", user.id)
                    putString("avatar", user.avatar)
                    putString("name", user.name)
                }
                findNavController().apply {
                    this.popBackStack(R.id.nav_users, true)
                    this.navigate(R.id.nav_profile, bundle)
                }
            }
        })

        binding.fragmentListUsers.adapter = adapter

        userViewModel.data.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        userViewModel.dataState.observe(viewLifecycleOwner) { state ->
            when {
                state.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
        return binding.root
    }
}