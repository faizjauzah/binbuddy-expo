package com.example.expopab.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class TrashReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        NotificationHelper(applicationContext).showNotification()
        return Result.success()
    }
}