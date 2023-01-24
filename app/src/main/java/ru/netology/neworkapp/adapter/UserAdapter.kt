package ru.netology.neworkapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.CardUserBinding
import ru.netology.neworkapp.dto.User

interface UserCallback {
    fun openProfile(user: User)
}

class UserAdapter(private val userCallback: UserCallback) :
    ListAdapter<User, UserViewHolder>(UserDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return UserViewHolder(binding, userCallback)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}

class UserViewHolder(
    private val binding: CardUserBinding,
    private val userCallback: UserCallback,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: User) {
        with(binding) {
            userName.text = user.name
            Glide.with(userAvatarCard)
                .load("${user.avatar}")
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_default_user_profile_image)
                .into(userAvatarCard)

            userView.setOnClickListener {
                userCallback.openProfile(user)
            }
        }
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {

    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}