package ru.netology.neworkapp.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import ru.netology.neworkapp.BuildConfig.BASE_URL
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.CardAdBinding
import ru.netology.neworkapp.databinding.CardPostBinding
import ru.netology.neworkapp.dto.Ad
import ru.netology.neworkapp.dto.FeedItem
import ru.netology.neworkapp.dto.Post
import ru.netology.neworkapp.utils.formatToDate
import ru.netology.neworkapp.utils.load


interface OnPostInteractionListener {
    fun onOpenPost(post: Post) {}
//    fun onLikePost(post: Post) {}
//    fun onEditPost(post: Post) {}
//    fun onRemovePost(post: Post) {}
//    fun onSharePost(post: Post) {}
//    fun onOpenImage(image: String) {}
}

class PostsAdapter(
    private val onPostInteractionListener: OnPostInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> error("Unknown item type")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                PostViewHolder(binding, onPostInteractionListener)
            }

            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AdViewHolder(binding)
            }
            else -> error("Unknown item type: $viewType")
        }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            null -> error("Unknown item type")
        }
    }

    class AdViewHolder(
        private val binding: CardAdBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ad: Ad) {
            binding.apply {
                binding.imageAd.load("${BASE_URL}/media/${ad.image}")
            }
        }
    }

    class PostViewHolder(
        private val binding: CardPostBinding,
        private val onPostInteractionListener: OnPostInteractionListener,
    ) : RecyclerView.ViewHolder(binding.root) {

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(post: Post) {

            with(binding) {
                textViewAuthorCardPost.text = post.author
                textViewPublishedCardPost.text = formatToDate(post.published)
                textViewContentCardPost.text = post.content

                Glide.with(imageViewAvatarCardPost)
                    .load("${post.authorAvatar}")
                    .transform(CircleCrop())
                    .placeholder(R.drawable.ic_default_user_profile_image)
                    .into(imageViewAvatarCardPost)
            }
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
//                menu.isVisible = post.ownedByMe
//                menu.setOnClickListener {
//                    PopupMenu(it.context, it).apply {
//                        inflate(R.menu.options_post)
//                        menu.setGroupVisible(R.id.owned, post.ownedByMe)
//                        setOnMenuItemClickListener { item ->
//                            when (item.itemId) {
//                                R.id.remove -> {
//                                    onPostInteractionListener.onRemovePost(post)
//                                    true
//                                }
//                                R.id.edit -> {
//                                    onPostInteractionListener.onEditPost(post)
//                                    true
//                                }
//
//                                else -> false
//                            }
//                        }
//                    }.show()
//                }
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
//            }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
        override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            if (oldItem::class != newItem::class) {
                return false
            }
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
            return oldItem == newItem
        }
    }
}