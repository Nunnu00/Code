package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ChatListBinding

class ChatListAdapter(val onItemClicked: (ChattingListModel) -> Unit) : ListAdapter<ChattingListModel, ChatListAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatListItem: ChattingListModel) {
            binding.root.setOnClickListener{
                onItemClicked(chatListItem)
            }
            binding.chatRoomTitleTextView.text = chatListItem.itemTitle
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ChatListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChattingListModel>() {

            override fun areItemsTheSame(oldItem: ChattingListModel, newItem: ChattingListModel): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(oldItem: ChattingListModel, newItem: ChattingListModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}