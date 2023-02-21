package ru.netology.neworkapp.activity

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.FragmentNewEventBinding
import ru.netology.neworkapp.utils.AndroidUtils
import ru.netology.neworkapp.utils.StringArg
import ru.netology.neworkapp.utils.formatToInstant
import ru.netology.neworkapp.viewmodel.EventViewModel
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewEventFragment : Fragment() {

    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val eventViewModel by activityViewModels<EventViewModel>()

    private var fragmentNewEventBinding: FragmentNewEventBinding? = null

    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentNewEventBinding.inflate(
            inflater,
            container,
            false
        )

        fragmentNewEventBinding = binding

        arguments?.textArg
            ?.let(binding.editTextFragmentNewEvent::setText)

        binding.editTextFragmentNewEvent.requestFocus()

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
                        eventViewModel.changePhoto(uri)
                    }
                }
            }

        binding.imageViewPickPhotoFragmentNewEvent.setOnClickListener {
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

        binding.imageViewTakePhotoFragmentNewEvent.setOnClickListener {
            ImagePicker.Builder(this)
                .cameraOnly()
                .maxResultSize(2048, 2048)
                .createIntent(photoLauncher::launch)
        }

        binding.buttonRemovePhotoFragmentNewEvent.setOnClickListener {
            eventViewModel.changePhoto(null)
        }

        eventViewModel.photo.observe(viewLifecycleOwner) {
            if (it?.uri == null) {
                binding.frameLayoutPhotoFragmentNewEvent.visibility = View.GONE
                return@observe
            }
            binding.frameLayoutPhotoFragmentNewEvent.visibility = View.VISIBLE
            binding.imageViewPhotoFragmentNewEvent.setImageURI(it.uri)
        }

        val datePicker = OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = monthOfYear
            calendar[Calendar.DAY_OF_MONTH] = dayOfMonth

            binding.editTextDateFragmentNewEvent.setText(
                SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
                    .format(calendar.time)
            )
        }

        binding.editTextDateFragmentNewEvent.setOnClickListener {
            context?.let {
                DatePickerDialog(
                    it,
                    datePicker,
                    calendar[Calendar.YEAR],
                    calendar[Calendar.MONTH],
                    calendar[Calendar.DAY_OF_MONTH]
                )
                    .show()
            }
        }

        val timePicker = OnTimeSetListener { _, hourOfDay, minute ->
            calendar[Calendar.HOUR_OF_DAY] = hourOfDay
            calendar[Calendar.MINUTE] = minute

            binding.editTextTimeFragmentNewEvent.setText(
                SimpleDateFormat("HH-mm", Locale.ROOT)
                    .format(calendar.time)
            )
        }

        binding.editTextTimeFragmentNewEvent.setOnClickListener {
            TimePickerDialog(
                context,
                timePicker,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            )
                .show()
        }

        eventViewModel.eventCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.create_post_menu, menu)
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        fragmentNewEventBinding?.let {
                            if (it.editTextFragmentNewEvent.text.isNullOrBlank()) {
                                Toast.makeText(
                                    activity,
                                    R.string.error_empty_content,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {
                                eventViewModel.changeContent(
                                    it.editTextFragmentNewEvent.text.toString(),
                                    formatToInstant(
                                        "${it.editTextDateFragmentNewEvent}" +
                                                "${it.editTextTimeFragmentNewEvent}"
                                    )
                                )
                                eventViewModel.save()
                                AndroidUtils.hideKeyboard(requireView())
                            }
                        }
                        true
                    }
                    else -> false
                }
        }, viewLifecycleOwner)

        return binding.root
    }

    override fun onDestroyView() {
        fragmentNewEventBinding = null
        super.onDestroyView()
    }
}