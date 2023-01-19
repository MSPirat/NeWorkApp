package ru.netology.neworkapp.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.neworkapp.R
import ru.netology.neworkapp.auth.AppAuth
import ru.netology.neworkapp.databinding.FragmentSignUpBinding
import ru.netology.neworkapp.viewmodel.SignUpViewModel
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SignUpFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentSignUpBinding.inflate(
            inflater,
            container,
            false
        )

        binding.buttonSignUp.setOnClickListener {
            if (
                binding.textFieldPassword.editText?.text.toString() ==
                binding.textFieldRepeatPassword.editText?.text.toString()
            ) {
                viewModel.registrationUser(
                    binding.textFieldLogin.editText?.text.toString(),
                    binding.textFieldPassword.editText?.text.toString(),
                    binding.textFieldName.editText?.text.toString()
                )
            } else
                binding.textFieldRepeatPassword.error =
                    getString(R.string.error_password)
        }

        viewModel.data.observe(viewLifecycleOwner) {
            appAuth.setAuth(it.id, it.token)
            findNavController().navigate(R.id.action_signUpFragment_to_appActivity)
        }

        return binding.root
    }
}