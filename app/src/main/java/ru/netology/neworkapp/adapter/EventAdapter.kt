package ru.netology.neworkapp.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Build
import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.neworkapp.R
import ru.netology.neworkapp.databinding.CardEventBinding
import ru.netology.neworkapp.dto.Event
import ru.netology.neworkapp.enumeration.AttachmentType
import ru.netology.neworkapp.utils.formatToDate

interface OnEventInteractionListener {
    fun onOpenEvent(event: Event)
    fun onEditEvent(event: Event)
    fun onRemoveEvent(event: Event)
    fun onOpenSpeakers(event: Event)
    fun onOpenMap(event: Event)
    fun onOpenImageAttachment(event: Event)
    fun onLikeEvent(event: Event)
    fun onParticipateEvent(event: Event)
    fun onShareEvent(event: Event)
    fun onOpenLikers(event: Event)
    fun onOpenParticipants(event: Event)
}

class EventAdapter(
    private val onEventInteractionListener: OnEventInteractionListener,
) : PagingDataAdapter<Event, EventViewHolder>(EventDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = CardEventBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EventViewHolder(binding, onEventInteractionListener)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }
}

class EventViewHolder(
    private val binding: CardEventBinding,
    private val onEventInteractionListener: OnEventInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun bind(event: Event) {

        binding.apply {
            textViewAuthorCardEvent.text = event.author
            textViewPublishedCardEvent.text = formatToDate(event.published)
            textViewContentCardEvent.text = event.content
            textViewDatetimeCardEvent.text = formatToDate(event.datetime)
            checkboxSpeakersSumCardEvent.text = event.speakerIds.count().toString()
            buttonLikeCardEvent.isChecked = event.likedByMe
            checkboxLikesSumCardEvent.text = event.likeOwnerIds.count().toString()
            buttonParticipateCardEvent.isChecked = event.participatedByMe
            checkboxParticipantsSumCardEvent.text = event.participantsIds.count().toString()

            imageViewAttachmentImageCardEvent.visibility =
                if (
                    event.attachment != null && event.attachment.type == AttachmentType.IMAGE
                ) VISIBLE else GONE

            Glide.with(itemView)
                .load("${event.authorAvatar}")
                .placeholder(R.drawable.ic_baseline_loading_24)
                .error(R.drawable.ic_default_user_profile_image)
                .timeout(10_000)
                .circleCrop()
                .into(imageViewAvatarCardEvent)

            event.attachment?.apply {
                Glide.with(imageViewAttachmentImageCardEvent)
                    .load(this.url)
                    .placeholder(R.drawable.ic_baseline_loading_24)
                    .error(R.drawable.ic_baseline_error_outline_24)
                    .timeout(10_000)
                    .into(imageViewAttachmentImageCardEvent)
            }

            buttonMenuCardEvent.isVisible = event.ownedByMe
            buttonMenuCardEvent.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    menu.setGroupVisible(R.id.owned, event.ownedByMe)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onEventInteractionListener.onRemoveEvent(event)
                                true
                            }
                            R.id.edit -> {
                                onEventInteractionListener.onEditEvent(event)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            checkboxSpeakersSumCardEvent.setOnClickListener {
                onEventInteractionListener.onOpenSpeakers(event)
            }

            imageViewAttachmentImageCardEvent.setOnClickListener {
                event.attachment?.let {
                    onEventInteractionListener.onOpenImageAttachment(event)
                }
            }

//            buttonLocationCardEvent.visibility =
//                if (
//                    event.coordinates == null ||
//                    event.coordinates.lat == 0.0 &&
//                    event.coordinates.long == 0.0
//                ) GONE else VISIBLE

            buttonLocationCardEvent.setOnClickListener {
                onEventInteractionListener.onOpenMap(event)
            }

            imageViewAttachmentImageCardEvent.setOnClickListener {
                onEventInteractionListener.onOpenImageAttachment(event)
            }

            buttonLikeCardEvent.setOnClickListener {
                val scaleX = PropertyValuesHolder.ofFloat(SCALE_X, 1F, 1.25F, 1F)
                val scaleY = PropertyValuesHolder.ofFloat(SCALE_Y, 1F, 1.25F, 1F)
                ObjectAnimator.ofPropertyValuesHolder(it, scaleX, scaleY).apply {
                    duration = 500
                    repeatCount = 1
                    interpolator = BounceInterpolator()
                }.start()
                onEventInteractionListener.onLikeEvent(event)
            }

            buttonParticipateCardEvent.setOnClickListener {
                onEventInteractionListener.onParticipateEvent(event)
            }

            buttonShareCardEvent.setOnClickListener {
                onEventInteractionListener.onShareEvent(event)
            }

            checkboxLikesSumCardEvent.setOnClickListener {
                onEventInteractionListener.onOpenLikers(event)
            }

            checkboxParticipantsSumCardEvent.setOnClickListener {
                onEventInteractionListener.onOpenParticipants(event)
            }
        }
    }
}

class EventDiffCallback : DiffUtil.ItemCallback<Event>() {
    override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
        return oldItem == newItem
    }
}