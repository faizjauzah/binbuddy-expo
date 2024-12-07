package com.example.expopab.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.expopab.databinding.FragmentTrackingBinding
import com.example.expopab.databinding.ItemReminderBinding
import com.example.expopab.notification.TrashReminderWorker
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit

data class ReminderTime(
    val id: String = UUID.randomUUID().toString(),
    val hour: Int,
    val minute: Int
)

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

class TrackingFragment : Fragment() {
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!
    private val reminderList = mutableListOf<ReminderTime>()
    private lateinit var adapter: ReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        binding.timePicker.setIs24HourView(true)
        binding.setReminderButton.setOnClickListener {
            val hour = binding.timePicker.hour
            val minute = binding.timePicker.minute

            val reminder = ReminderTime(hour = hour, minute = minute)
            reminderList.add(reminder)
            adapter.notifyItemInserted(reminderList.size - 1)

            scheduleReminder(hour, minute)
            Toast.makeText(context, "Reminder set for $hour:$minute", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = ReminderAdapter(reminderList) { reminder ->
            val position = reminderList.indexOf(reminder)
            reminderList.remove(reminder)
            adapter.notifyItemRemoved(position)
            // Optional: Cancel the WorkManager task here
        }

        binding.reminderList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@TrackingFragment.adapter
        }
    }

    private fun scheduleReminder(hour: Int, minute: Int) {
        val workManager = WorkManager.getInstance(requireContext())

        val calendar = Calendar.getInstance()
        val now = calendar.clone() as Calendar

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute - 10)
        calendar.set(Calendar.SECOND, 0)

        if (calendar.before(now)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val delayMillis = calendar.timeInMillis - now.timeInMillis

        val reminderWork = OneTimeWorkRequestBuilder<TrashReminderWorker>()
            .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueue(reminderWork)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}