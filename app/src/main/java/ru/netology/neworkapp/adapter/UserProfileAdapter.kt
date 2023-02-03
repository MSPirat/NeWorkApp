package ru.netology.neworkapp.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.neworkapp.activity.EventsFragment
import ru.netology.neworkapp.activity.JobsFragment
import ru.netology.neworkapp.activity.FeedFragment

@OptIn(ExperimentalCoroutinesApi::class)
class UserProfileAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val itemCount = 3

    override fun getItemCount(): Int {
        return this.itemCount
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return FeedFragment()
            1 -> return EventsFragment()
            2 -> return JobsFragment()
        }
        return FeedFragment()
    }
}