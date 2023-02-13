package ru.netology.neworkapp.activity

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.FragmentNewPostBinding
import ru.netology.neworkapp.utils.AndroidUtils
import ru.netology.neworkapp.utils.StringArg
import ru.netology.neworkapp.viewmodel.PostViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewPostFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val postViewModel by activityViewModels<PostViewModel>()

    private var fragmentNewPostBinding: FragmentNewPostBinding? = null

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

        fragmentNewPostBinding = binding

        arguments?.textArg
            ?.let(binding.editTextFragmentNewPost::setText)

        binding.editTextFragmentNewPost.requestFocus()

        val photoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                when (it.resultCode) {
                    ImagePicker.RESULT_ERROR -> {
                        Snackbar.make(
                            binding.root,
                            ImagePicker.getError(it.data),
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    else -> {
                        val uri: Uri? = it.data?.data
                        postViewModel.changePhoto(uri)
                    }
                }
            }

        binding.imageViewPickPhotoFragmentNewPost.setOnClickListener {
            ImagePicker.Builder(this)
                .galleryOnly()
                .galleryMimeTypes(
                    arrayOf(
                        "image/png",
                        "image/jpeg",
                        "image/jpg"
                    )
                )
                .maxResultSize(2048, 2048)
                .createIntent(photoLauncher::launch)
        }

        binding.imageViewTakePhotoFragmentNewPost.setOnClickListener {
            ImagePicker.Builder(this)
                .cameraOnly()
                .maxResultSize(2048, 2048)
                .createIntent(photoLauncher::launch)
        }

        binding.buttonRemovePhotoFragmentNewPost.setOnClickListener {
            postViewModel.changePhoto(null)
        }

        postViewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        postViewModel.photo.observe(viewLifecycleOwner) {
            if (it?.uri == null) {
                binding.frameLayoutPhotoFragmentNewPost.visibility = View.GONE
                return@observe
            }
            binding.frameLayoutPhotoFragmentNewPost.visibility = View.VISIBLE
            binding.imageViewPhotoFragmentNewPost.setImageURI(it.uri)
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.create_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        postViewModel.changeContent(
                            binding.editTextFragmentNewPost.text.toString()
                        )
                        postViewModel.save()
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }
                    else -> false
                }
        }, viewLifecycleOwner)

//        binding.editTextFragmentNewPost.setText(arguments?.getString("New Post"))
//
//        binding.fabOk.setOnClickListener {
//            if (binding.editTextFragmentNewPost.text.isNullOrBlank()) {
//                Toast.makeText(
//                    activity,
//                    R.string.error_empty_post,
//                    Toast.LENGTH_SHORT
//                ).show()
//            } else {
//                postViewModel.changeContent(binding.editTextFragmentNewPost.text.toString())
//                postViewModel.save()
//                AndroidUtils.hideKeyboard(requireView())
//                findNavController().navigateUp()
//            }
//        }

        return binding.root
    }

    override fun onDestroyView() {
        fragmentNewPostBinding = null
        super.onDestroyView()
    }
}