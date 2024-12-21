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

    class ViewHolder(val binding: ItemReminderBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dayTextViews = listOf(
            binding.minggu,
            binding.senin,
            binding.selasa,
            binding.rabu,
            binding.kamis,
            binding.jumat,
            binding.sabtu
        )

        fun bind(reminder: ReminderTime) {
            binding.timeText.text = String.format("%02d:%02d", reminder.hour, reminder.minute)

            // Update day indicators
            dayTextViews.forEachIndexed { index, textView ->
                val isSelected = reminder.selectedDays.contains(index)
                textView.isSelected = isSelected
                textView.setTextColor(
                    if (isSelected)
                        textView.context.getColor(android.R.color.white)
                    else
                        textView.context.getColor(android.R.color.black)
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(reminders[position])
        holder.binding.deleteButton.setOnClickListener { onDeleteClick(reminders[position]) }
    }

    override fun getItemCount() = reminders.size
}