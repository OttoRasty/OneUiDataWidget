package com.example.oneuidatawidget

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

class DailyWorker(ctx: Context, p: WorkerParameters) : CoroutineWorker(ctx, p) {
    override suspend fun doWork(): Result {
        DataWidgetProvider.updateAll(applicationContext)
        return Result.success()
    }

    companion object {
        fun schedule(ctx: Context) {
            val req = PeriodicWorkRequestBuilder<DailyWorker>(24, TimeUnit.HOURS)
                .setInitialDelay(1, TimeUnit.HOURS)
                .addTag("daily_tick")
                .build()
            WorkManager.getInstance(ctx).enqueueUniquePeriodicWork("daily_tick", ExistingPeriodicWorkPolicy.UPDATE, req)
        }
    }
}
