package com.example.myunievents.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.myunievents.ServiceLocator

class SyncWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return try {
            val repo = ServiceLocator.provideEventRepo(applicationContext)
            val list = repo.unpublished()
            list.forEach { e ->
                try { repo.pushToCloud(e) } catch (_: Exception) {}
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
