package ru.netology.neworkapp.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.neworkapp.R
import ru.netology.neworkapp.auth.AppAuth
import ru.netology.neworkapp.databinding.FragmentSignInBinding
import ru.netology.neworkapp.utils.AndroidUtils.hideKeyboard
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

        with(binding) {
            textFieldLogin.requestFocus()
            buttonSignIn.setOnClickListener {
                viewModel.authorizationUser(
                    textFieldLogin.editText?.text.toString(),
                    textFieldPassword.editText?.text.toString()
                )
                hideKeyboard(requireView())
            }
        }

        binding.textFieldPassword.setErrorIconOnClickListener {
            binding.textFieldPassword.error = null
        }

        viewModel.data.observe(viewLifecycleOwner) {
            appAuth.setAuth(it.id, it.token)
            findNavController().navigateUp()
        }

        binding.buttonSignUp.setOnClickListener {
            findNavController().navigate(R.id.nav_sign_up_fragment)
        }

        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            when {
                state.loginError -> {
                    binding.textFieldPassword.error = getString(R.string.error_login)
                }
                state.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        return binding.root
    }
}