package ru.netology.neworkapp.activity

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.dhaval2404.imagepicker.constant.ImageProvider
import com.google.android.material.snackbar.Snackbar
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

        val pickPhotoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    Activity.RESULT_OK -> {
                        val uri: Uri? = it.data?.data
                        viewModel.changePhoto(uri)
                    }
                }
            }

        binding.profileImage.setOnClickListener {
            ImagePicker.with(this)
                .crop()
                .compress(2048)
                .provider(ImageProvider.BOTH)
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                        "image/jpg"
                    )
                )
                .createIntent(pickPhotoLauncher::launch)
        }

        viewModel.data.observe(viewLifecycleOwner) {
            appAuth.setAuth(it.id, it.token)
            findNavController().navigate(R.id.action_signUpFragment_to_appActivity)
        }

        viewModel.photo.observe(viewLifecycleOwner) {
            if (it.uri == null) {
                return@observe
            }
            binding.profileImage.setImageURI(it.uri)
        }

        return binding.root
    }
}