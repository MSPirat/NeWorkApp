package ru.netology.neworkapp.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.CardPostBinding
import ru.netology.neworkapp.dto.Post
import ru.netology.neworkapp.utils.formatToDate


interface OnPostInteractionListener {
    fun onOpenPost(post: Post) {}
    fun onEditPost(post: Post) {}
    fun onRemovePost(post: Post) {}
//    fun onLikePost(post: Post) {}
//    fun onSharePost(post: Post) {}
//    fun onOpenImage(image: String) {}
}

class PostsAdapter(
    private val onPostInteractionListener: OnPostInteractionListener,
) : PagingDataAdapter<Post, PostViewHolder>(PostDiffCallback()) {

//    override fun getItemViewType(position: Int): Int =
//        when (getItem(position)) {
//            is Post -> R.layout.card_post
//            else -> error("Unknown item type")
//        }

//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder =
//        when (viewType) {
//            R.layout.card_post -> {
//                val binding =
//                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//                PostViewHolder(binding, onPostInteractionListener)
//            }
//            else -> error("Unknown item type: $viewType")
//        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding, onPostInteractionListener)
    }

    //    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
//        when (val item = getItem(position)) {
//            is Post -> (holder as? PostViewHolder)?.bind(item)
//            null -> error("Unknown item type")
//        }
//    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onPostInteractionListener: OnPostInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(post: Post) {

        binding.apply {
            textViewAuthorCardPost.text = post.author
            textViewPublishedCardPost.text = formatToDate(post.published)
            textViewContentCardPost.text = post.content

            Glide.with(imageViewAvatarCardPost)
                .load("${post.authorAvatar}")
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_default_user_profile_image)
                .into(imageViewAvatarCardPost)
//        }
//            binding.apply {
//                author.text = post.author
//                published.text = post.published
//                content.text = post.content
//                like.isChecked = post.likedByMe
//                like.text = "${post.likes}"
//                attachment.visibility = View.GONE
//
//                val url = "${BASE_URL}/avatars/${post.authorAvatar}"
//                Glide.with(itemView)
//                    .load(url)
//                    .placeholder(R.drawable.ic_loading_24dp)
//                    .error(R.drawable.ic_baseline_error_outline_24dp)
//                    .timeout(10_000)
//                    .circleCrop()
//                    .into(avatar)
//
//                val urlAttachment = "${BASE_URL}/media/${post.attachment?.url}"
//                if (post.attachment != null) {
//                    attachment.visibility = View.VISIBLE
//                    Glide.with(itemView)
//                        .load(urlAttachment)
//                        .placeholder(R.drawable.ic_loading_24dp)
//                        .error(R.drawable.ic_baseline_error_outline_24dp)
//                        .timeout(10_000)
//                        .into(attachment)
//                } else {
//                    attachment.visibility = View.GONE
//                }
//

            buttonMenuCardPost.isVisible = post.ownedByMe
            buttonMenuCardPost.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, post.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onPostInteractionListener.onRemovePost(post)
                                true
                            }
                            R.id.edit -> {
                                onPostInteractionListener.onEditPost(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }
//
//                like.setOnClickListener {
////                    onInteractionListener.onLike(post)
//                    like.setOnClickListener {
//                        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1F, 1.25F, 1F)
//                        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1F, 1.25F, 1F)
//                        ObjectAnimator.ofPropertyValuesHolder(it, scaleX, scaleY).apply {
//                            duration = 500
//                            repeatCount = 100
//                            interpolator = BounceInterpolator()
//                        }.start()
//                        onPostInteractionListener.onLikePost(post)
//                    }
//                }
//
//                share.setOnClickListener {
//                    onPostInteractionListener.onSharePost(post)
//                }
//
//                attachment.setOnClickListener {
//                    post.attachment?.let { attach ->
//                        onPostInteractionListener.onOpenImage(attach.url)
//                    }
//                }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}
