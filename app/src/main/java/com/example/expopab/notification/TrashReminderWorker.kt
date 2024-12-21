package com.example.expopab.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class TrashReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val isEarlyReminder = inputData.getBoolean("isEarlyReminder", true)

        if (isEarlyReminder) {
            NotificationHelper(applicationContext).showEarlyNotification()
        } else {
            NotificationHelper(applicationContext).showOnTimeNotification()
        }

        return Result.success()
    }
}