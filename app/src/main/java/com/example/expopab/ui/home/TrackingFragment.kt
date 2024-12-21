package com.example.expopab.ui.home
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
            saveReminder(binding.timePicker.hour, binding.timePicker.minute)
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
            userId = getCurrentUserId()
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

        // Set up calendar for current time
        val calendar = Calendar.getInstance()
        val now = calendar.clone() as Calendar

        // Schedule early reminder (10 minutes before)
        val earlyCalendar = calendar.clone() as Calendar
        earlyCalendar.set(Calendar.HOUR_OF_DAY, hour)
        earlyCalendar.set(Calendar.MINUTE, minute - 10)
        earlyCalendar.set(Calendar.SECOND, 0)

        if (earlyCalendar.before(now)) {
            earlyCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Schedule on-time reminder
        val onTimeCalendar = calendar.clone() as Calendar
        onTimeCalendar.set(Calendar.HOUR_OF_DAY, hour)
        onTimeCalendar.set(Calendar.MINUTE, minute)
        onTimeCalendar.set(Calendar.SECOND, 0)

        if (onTimeCalendar.before(now)) {
            onTimeCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Calculate delays
        val earlyDelayMillis = earlyCalendar.timeInMillis - now.timeInMillis
        val onTimeDelayMillis = onTimeCalendar.timeInMillis - now.timeInMillis

        // Create data for workers
        val earlyData = workDataOf("isEarlyReminder" to true)
        val onTimeData = workDataOf("isEarlyReminder" to false)

        // Create work requests
        val earlyReminderWork = OneTimeWorkRequestBuilder<TrashReminderWorker>()
            .setInitialDelay(earlyDelayMillis, TimeUnit.MILLISECONDS)
            .setInputData(earlyData)
            .build()

        val onTimeReminderWork = OneTimeWorkRequestBuilder<TrashReminderWorker>()
            .setInitialDelay(onTimeDelayMillis, TimeUnit.MILLISECONDS)
            .setInputData(onTimeData)
            .build()

        // Enqueue both work requests
        workManager.enqueue(listOf(earlyReminderWork, onTimeReminderWork))
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
                snapshot?.forEach { document ->
                    val reminder = document.toObject(ReminderTime::class.java)
                    reminderList.add(reminder)
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