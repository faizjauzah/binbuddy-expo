package com.example.expopab.ui.home
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.expopab.databinding.FragmentTrackingBinding
import com.example.expopab.databinding.ItemReminderBinding
import com.example.expopab.notification.TrashReminderWorker
import com.example.expopab.ui.home.adapter.ReminderAdapter
import com.example.expopab.ui.home.data.ReminderTime
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import java.util.*
import java.util.concurrent.TimeUnit


class TrackingFragment : Fragment() {
    private var _binding: FragmentTrackingBinding? = null
    private val binding get() = _binding!!
    private val reminderList = mutableListOf<ReminderTime>()
    private lateinit var adapter: ReminderAdapter
    private val db = Firebase.firestore
    private val selectedDays = mutableSetOf<Int>()
    private lateinit var dayButtons: List<TextView>

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
        setupDaySelectors()

        binding.timePicker.setIs24HourView(true)
        binding.setReminderButton.setOnClickListener {
            if (selectedDays.isEmpty()) {
                Toast.makeText(context, "Please select at least one day", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            saveReminder(binding.timePicker.hour, binding.timePicker.minute)
        }
    }

    private fun setupDaySelectors() {
        dayButtons = listOf(
            binding.minggu,
            binding.senin,
            binding.selasa,
            binding.rabu,
            binding.kamis,
            binding.jumat,
            binding.sabtu
        )

        dayButtons.forEachIndexed { index, textView ->
            textView.setOnClickListener {
                if (selectedDays.contains(index)) {
                    selectedDays.remove(index)
                    updateDayButtonState(textView, false)
                } else {
                    selectedDays.add(index)
                    updateDayButtonState(textView, true)
                }
            }
        }
    }

    private fun updateDayButtonState(textView: TextView, isSelected: Boolean) {
        textView.isSelected = isSelected
        // Update text color based on selection
        textView.setTextColor(
            if (isSelected)
                ContextCompat.getColor(requireContext(), android.R.color.white)
            else
                ContextCompat.getColor(requireContext(), android.R.color.black)
        )
    }

    // When loading existing reminder, update button states
    private fun updateDayButtonsFromReminder(reminder: ReminderTime) {
        dayButtons.forEachIndexed { index, button ->
            val isSelected = reminder.selectedDays.contains(index)
            updateDayButtonState(button, isSelected)
        }
    }

    private fun setupRecyclerView() {
        adapter = ReminderAdapter(reminderList) { reminder ->
            deleteReminder(reminder)
        }

        binding.reminderList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@TrackingFragment.adapter
        }
    }

    private fun saveReminder(hour: Int, minute: Int) {
        val reminder = ReminderTime(
            hour = hour,
            minute = minute,
            userId = getCurrentUserId(),
            selectedDays = selectedDays.toList()
        )

        db.collection("reminders")
            .document(reminder.id)
            .set(reminder)
            .addOnSuccessListener {
                scheduleReminder(hour, minute)
                Toast.makeText(context, "Reminder set for $hour:$minute", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                Log.e("TrackingFragment", "Failed to save reminder", e)
            }
    }

    private fun deleteReminder(reminder: ReminderTime) {
        try {
            db.collection("reminders")
                .document(reminder.id)
                .delete()
                .addOnSuccessListener {
                    // Successfully deleted
                }
                .addOnFailureListener { e ->
                    Log.e("TrackingFragment", "Error deleting reminder", e)
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Log.e("TrackingFragment", "Error in deleteReminder", e)
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun scheduleReminder(hour: Int, minute: Int) {
        val workManager = WorkManager.getInstance(requireContext())

        selectedDays.forEach { dayOfWeek ->
            // Calculate next occurrence of this day
            val calendar = Calendar.getInstance()
            val now = calendar.clone() as Calendar

            while (calendar.get(Calendar.DAY_OF_WEEK) != dayOfWeek + 1) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)

            if (calendar.before(now)) {
                calendar.add(Calendar.WEEK_OF_YEAR, 1)
            }

            val earlyCalendar = calendar.clone() as Calendar
            earlyCalendar.add(Calendar.MINUTE, -10)

            // Calculate delays
            val earlyDelayMillis = earlyCalendar.timeInMillis - now.timeInMillis
            val onTimeDelayMillis = calendar.timeInMillis - now.timeInMillis

            // Create work requests for this day
            val earlyData = workDataOf("isEarlyReminder" to true)
            val onTimeData = workDataOf("isEarlyReminder" to false)

            val earlyReminderWork = OneTimeWorkRequestBuilder<TrashReminderWorker>()
                .setInitialDelay(earlyDelayMillis, TimeUnit.MILLISECONDS)
                .setInputData(earlyData)
                .build()

            val onTimeReminderWork = OneTimeWorkRequestBuilder<TrashReminderWorker>()
                .setInitialDelay(onTimeDelayMillis, TimeUnit.MILLISECONDS)
                .setInputData(onTimeData)
                .build()

            workManager.enqueue(listOf(earlyReminderWork, onTimeReminderWork))
        }
    }

    private fun getCurrentUserId(): String {
        return Firebase.auth.currentUser?.uid ?: "default_user"
    }

    private fun loadReminders() {
        db.collection("reminders")
            .whereEqualTo("userId", getCurrentUserId())
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Toast.makeText(context, "Error loading reminders", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                reminderList.clear()
                snapshot?.documents?.mapNotNull { document ->
                    document.toObject(ReminderTime::class.java)
                }?.sortedWith(
                    compareByDescending<ReminderTime> {
                        it.createdAt
                    }.thenBy {
                        it.hour
                    }.thenBy {
                        it.minute
                    }
                )?.let {
                    reminderList.addAll(it)
                }
                adapter.notifyDataSetChanged()
            }
    }

    override fun onResume() {
        super.onResume()
        loadReminders()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}