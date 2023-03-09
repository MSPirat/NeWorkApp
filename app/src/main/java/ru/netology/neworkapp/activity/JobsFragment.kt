package ru.netology.neworkapp.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.neworkapp.R
import ru.netology.neworkapp.adapter.JobAdapter
import ru.netology.neworkapp.adapter.OnJobInteractionListener
import ru.netology.neworkapp.databinding.FragmentJobsBinding
import ru.netology.neworkapp.dto.Job
import ru.netology.neworkapp.viewmodel.JobViewModel

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class JobsFragment : Fragment() {

    private val jobViewModel by activityViewModels<JobViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val binding = FragmentJobsBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = JobAdapter(object : OnJobInteractionListener {

            override fun onRemoveJob(job: Job) {
                jobViewModel.removeById(job.id)
            }

            override fun onEditJob(job: Job) {
                jobViewModel.edit(job)
                val bundle = Bundle().apply {
                    putString("name", job.name)
                    putString("position", job.position)
                    putLong("start", job.start)
                    job.finish?.let {
                        putLong("finish", it)
                    }
                    job.link?.let {
                        putString("link", it)
                    }
                }
                findNavController()
                    .navigate(R.id.nav_new_job_fragment, bundle)
            }
        })

        val id = parentFragment?.arguments?.getLong("id")

        binding.recyclerViewContainerFragmentJobs.adapter = adapter

        lifecycleScope.launchWhenCreated {
            if (id != null) {
                jobViewModel.setId(id)
                jobViewModel.loadJobs(id)
            }
            jobViewModel.data.collectLatest {
                adapter.submitList(it)
                binding.textViewEmptyTextFragmentJobs.isVisible =
                    it.isEmpty()
            }
        }

        return binding.root
    }
}