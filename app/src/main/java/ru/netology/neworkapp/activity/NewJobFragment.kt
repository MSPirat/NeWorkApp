package ru.netology.neworkapp.activity

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.FragmentNewJobBinding
import ru.netology.neworkapp.utils.AndroidUtils
import ru.netology.neworkapp.utils.dateToEpochSec
import ru.netology.neworkapp.utils.epochSecToDate
import ru.netology.neworkapp.utils.pickDate
import ru.netology.neworkapp.viewmodel.JobViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class NewJobFragment : Fragment() {

    private val jobViewModel by activityViewModels<JobViewModel>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentNewJobBinding.inflate(
            inflater,
            container,
            false
        )

        (activity as AppCompatActivity).supportActionBar?.title =
            context?.getString(R.string.title_job)

        val name = arguments?.getString("name")
        val position = arguments?.getString("position")
        val start = arguments?.getLong("start")
        val finish = arguments?.getLong("finish")
        val link = arguments?.getString("link")

        binding.editTextNameFragmentNewJob.setText(name)
        binding.editTextPositionFragmentNewJob.setText(position)
        binding.editTextStartFragmentNewJob.setText(start?.let { epochSecToDate(it) })
        binding.editTextFinishFragmentNewJob.setText(
            if (finish == 0L) " " else finish?.let { epochSecToDate(it) }
        )
        binding.editTextLinkFragmentNewJob.setText(link)


        binding.buttonSaveFragmentNewJob.setOnClickListener {
            binding.let {
                if (
                    it.editTextNameFragmentNewJob.text.isNullOrBlank() ||
                    it.editTextPositionFragmentNewJob.text.isNullOrBlank() ||
                    it.editTextStartFragmentNewJob.text.isNullOrBlank()
                ) {
                    Toast.makeText(
                        activity,
                        R.string.error_field_required,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    jobViewModel.changeJobData(
                        it.editTextNameFragmentNewJob.text.toString(),
                        it.editTextPositionFragmentNewJob.text.toString(),
                        dateToEpochSec(it.editTextStartFragmentNewJob.text.toString())!!,
                        dateToEpochSec(it.editTextFinishFragmentNewJob.text.toString()),
                        it.editTextLinkFragmentNewJob.text.toString(),
                    )
                    jobViewModel.save()
                    AndroidUtils.hideKeyboard(requireView())
                }
            }
        }

        binding.editTextStartFragmentNewJob.setOnClickListener {
            context?.let { item ->
                pickDate(binding.editTextStartFragmentNewJob, item)
            }
        }

        binding.editTextFinishFragmentNewJob.setOnClickListener {
            context?.let { item ->
                pickDate(binding.editTextFinishFragmentNewJob, item)
            }
        }

        jobViewModel.jobCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        jobViewModel.dataState.observe(viewLifecycleOwner) {
            when {
                it.error -> {
                    Toast.makeText(context, R.string.error_loading, Toast.LENGTH_SHORT)
                        .show()
                }
            }
            binding.progressBarFragmentNewJob.isVisible = it.loading
        }

        return binding.root
    }
}