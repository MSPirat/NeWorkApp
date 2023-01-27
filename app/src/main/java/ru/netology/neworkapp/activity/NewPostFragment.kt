package ru.netology.neworkapp.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.FragmentNewPostBinding
import ru.netology.neworkapp.utils.AndroidUtils
import ru.netology.neworkapp.viewmodel.PostViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewPostFragment : Fragment() {

    private val postViewModel by activityViewModels<PostViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        binding.editTextFragmentNewPost.setText(arguments?.getString("New Post"))

        binding.fabOk.setOnClickListener {
            if (binding.editTextFragmentNewPost.text.isNullOrBlank()) {
                Toast.makeText(
                    activity,
                    R.string.error_empty_post,
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                postViewModel.changeContent(binding.editTextFragmentNewPost.text.toString())
                postViewModel.save()
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            }
        }

        postViewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        return binding.root
    }
}