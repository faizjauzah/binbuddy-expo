package com.example.expopab.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.expopab.databinding.ItemReminderBinding
import com.example.expopab.ui.home.data.ReminderTime

class ReminderAdapter(
    private val reminders: List<ReminderTime>,
    private val onDeleteClick: (ReminderTime) -> Unit
) : RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reminder = reminders[position]
        holder.binding.timeText.text = String.format("%02d:%02d", reminder.hour, reminder.minute)
        holder.binding.deleteButton.setOnClickListener { onDeleteClick(reminder) }
    }

    override fun getItemCount() = reminders.size
}