package com.example.expopab.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.expopab.R
import com.example.expopab.notification.TrashReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReminderFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reminder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val timePicker = view.findViewById<TimePicker>(R.id.timePicker)
        val setButton = view.findViewById<Button>(R.id.setReminderButton)

        setButton.setOnClickListener {
            scheduleReminder(timePicker.hour, timePicker.minute)
        }
    }

    private fun scheduleReminder(hour: Int, minute: Int) {
        val workManager = WorkManager.getInstance(requireContext())

        val calendar = Calendar.getInstance()
        val now = calendar.clone() as Calendar

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute - 10) // 10 minutes before
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
}