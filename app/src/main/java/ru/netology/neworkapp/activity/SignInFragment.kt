package ru.netology.neworkapp.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.neworkapp.R
import ru.netology.neworkapp.auth.AppAuth
import ru.netology.neworkapp.databinding.FragmentSignInBinding
import ru.netology.neworkapp.viewmodel.SignInViewModel
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignInFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel by viewModels<SignInViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentSignInBinding.inflate(
            inflater,
            container,
            false
        )

        binding.buttonSignIn.setOnClickListener {
            viewModel.updateUser(
                binding.textFieldLogin.editText?.text.toString(),
                binding.textFieldPassword.editText?.text.toString()
            )
        }

        binding.textFieldPassword.setErrorIconOnClickListener {
            binding.textFieldPassword.error = null
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            if (state.loginError) {
                binding.textFieldPassword.error = getString(R.string.error_login)
            }
        }

        requireActivity().findViewById<BottomNavigationView>(R.id.nav_view).visibility =
            View.GONE

        return binding.root
    }

    override fun onDestroy() {
        requireActivity().findViewById<BottomNavigationView>(R.id.nav_view).visibility =
            View.VISIBLE

        super.onDestroy()
    }
}