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
import com.example.expopab.databinding.FragmentTrackingBinding
import com.example.expopab.databinding.ItemReminderBinding
import com.example.expopab.notification.TrashReminderWorker
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.ktx.auth
import java.util.*
import java.util.concurrent.TimeUnit

data class ReminderTime(
    val id: String = UUID.randomUUID().toString(),
    val hour: Int = 0,
    val minute: Int = 0,
    val userId: String? = null
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